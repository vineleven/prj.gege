package gege.game;

import gege.common.StateData;
import gege.consts.Cmd;
import gege.consts.GameState;
import gege.game.Room.Visitor;
import gege.util.Logger;
import gege.util.Mathf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * 避免多线程问题，原则上游戏开始了，不对player进行增删操作
 * 
 * @author vineleven
 *
 */
public class World {
	
	private static int m_index = 0;
	final public int index;
	
	
	final private static int STATE_NORMAL = -1;
	// 红方
	final private static int STATE_RED = 0;
	// 蓝方
	final private static int STATE_BLUE = 1;
	
	
	private Map m_map;
	
	private int[] m_scores;
	
	private ArrayList<ArrayList<Player>> m_group = new ArrayList<ArrayList<Player>>(2);;
	
	private LinkedList<GameItem> m_items = new LinkedList<>();
	
	
	private float m_speed = 3f / 1000;
	
	// 游戏结束时间
	private long m_gameOverTime = 0;
	
	// 创建道具的时间
	private long m_createItemTime = 0;
	
	
	protected int m_state = STATE_NORMAL;
	
	
	private boolean m_bStart = false;
	
	
	
	public World() {
		index = m_index++;
	}
	
	
	public void createWorld(Room room){
		m_group.clear();
		
		int col = 15;
		int row = 15;
		
		// 地图
		m_map = new Map(row, col, 2);
		// 玩家
		int sideCount = room.getSideCount();
		for (int i = 0; i < 2; i++) {
			m_group.add(new ArrayList<Player>(sideCount));
		}
		room.forEach(this::addPlayer);
		room.clear();
		
		// 根据地图随机玩家位置
		HashSet<Integer> record = new HashSet<>();
		int rnd;
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < sideCount;) {
				rnd = m_map.getEmptyPosByInt();
				if(record.contains(rnd))
					continue;
				
				// 此处有风险
				if(rnd == -1)
					continue;
				
				Vector3 pos = m_map.getVector3ByInt(rnd);
				
				m_group.get(j).get(i).init(j, pos.x, pos.y, m_speed);
				record.add(rnd);
				i++;
			}
		}
		
		m_scores = new int[2];
		
		// 通知所有玩家(先准备再开始，如果需要等待所有玩家加载完成了再开始游戏，则可以在这里通知)
		// 目前采用start的时候统一通知，不用ack
	}
	
	
	private void addPlayer(Visitor v){
		int group = v.getGroup();
		int idx = m_group.get(group).size();
		v.getSession().setState(GameState.IN_GAME, new StateData(this.index, group, idx));
		m_group.get(group).add(new Player(v.getSession(), idx));
	}
	
	
	public boolean empty(){
		for (int i = 0; i < m_group.size(); i++) {
			for (int j = 0; j < m_group.get(i).size(); j++) {
				if(m_group.get(i).get(j).isOnline())
					return false;
			}
		}
		
		closeWorld();
		
		return true;
	}
	
	
	public void gm2GameOver(){
		onGameOver();
	}
	
	
	private void onGameOver(){
		JSONObject data = new JSONObject();
		int result = -1;
		if(m_scores[0] > m_scores[1]){
			result = 0;
		} else if(m_scores[0] < m_scores[1]){
			result = 1;
		}
		
		data.put("g", result);
		foreach(player -> {
			player.send(Cmd.S2C_GAME_OVER, data);
		});
		
		closeWorld();
	}
	
	
	public void closeWorld(){
		foreach(player -> {
			if(!player.isDisposed()){
				player.getSession().setOnDisconnect(null);
				player.getSession().setState(GameState.IDLE, null);
			}
		});
		
		m_map = null;
		m_group.clear();
		m_bStart = false;
		m_state = STATE_NORMAL;
	}
	
	
	public boolean inState(int state){
		return m_state == state;
	}
	
	
	public boolean isStart(){
		return m_bStart;
	}
	
	
	public Player getPlayer(int group, int index){
		return m_group.get(group).get(index);
	}
	
	
	public void foreach(Consumer<Player> action){
		for (int i = 0; i < m_group.size(); i++) {
			m_group.get(i).forEach(action);
		}
	}
	
	
	private void notifyStartInfo(int delay){
		JSONObject data = new JSONObject();
		data.put("map", m_map.toJSONArray());
		
		JSONArray players = new JSONArray();
		foreach(p -> {
			players.put(p.getStartInfo());
		});
		
		data.put("list", players);
		data.put("time", Game.getInstance().getCurTime() + delay);
		
		foreach(p -> {
			if(!p.isDisposed()){
				data.put("idx", p.getIndex());
				data.put("group", p.getGroup());
				p.getSession().send(Cmd.C2S_START_GAME, data);
			}
		});
	}
	
	
	public void start(){
		int delay = 3000;
		notifyStartInfo(delay);
		Game.getInstance().callLaterTime(delay, this::onStart);
		m_state = STATE_NORMAL;
	}
	
	
	private void onStart(){
		foreach(p->{
			p.goGoGo();
		});
		m_bStart = true;
		// 1分钟游戏时间
		m_gameOverTime = Game.getInstance().getCurTime() + 60000;
	}
	
	
	public void update() {
		if(!m_bStart)
			return;
		
		foreach(this::updatePlayer);
		collisionAllPlayer();
		
		updateItem();
		collisionItems();
		
		if(m_gameOverTime <= Game.getInstance().getCurTime())
			onGameOver();
	}
	
	
	private void updatePlayer(Player player){
		player.update();
	}
	
	
	private void updateItem(){
		if(m_createItemTime > Game.getInstance().getCurTime())
			return;
		
		// 5秒钟刷一次道具
		Vector3 pos = m_map.getEmptyPos();
		for (int j = 0; j < m_items.size(); j++) {
			if(m_items.get(j).inPos(pos)){
				return;
			}
		}
		
		// 随机一个物品
		GameItem item = new GameItem(Mathf.randomInt(0, 2));
		item.setPosition(pos.x, pos.y);
		
		m_items.add(item);
		
		JSONObject data = new JSONObject();
		data.put("x", pos.x);
		data.put("y", pos.y);
		data.put("t", item.getType());
		data.put("id", item.getId());
		
		foreach(player->{
			player.send(Cmd.S2C_NEW_ITEM, data);
		});
		
		m_createItemTime = Game.getInstance().getCurTime() + 5000;
	}
	
	
	private void collisionAllPlayer(){
		int len = m_group.size();
		for (int i = 0; i < len - 1; i++) {
			for (int j = i + 1; j < len; j++) {
				collisionGroup(m_group.get(i), m_group.get(j));
			}
		}
	}
	
	
	private void collisionGroup(ArrayList<Player> group1, ArrayList<Player> group2){
		// 这里其实已经保证了同阵营不会发生碰撞
		for (Player player1 : group1) {
			if(!player1.isNormal())
				continue;
			
			for (Player player2 : group2) {
				if(!player2.isNormal())
					continue;
				
				if(player1.tryCollision(player2)){
					if(inState(player1.getGroup())){
						notifyWinnerAndLoser(player1, player2);
					} else if(inState(player2.getGroup())){
						notifyWinnerAndLoser(player2, player1);
					}
				}
			}
		}
	}
	
	
	private void notifyWinnerAndLoser(Player winner, Player loser){
		Vector3 relivePos = m_map.getEmptyPos();
		loser.dead(relivePos.x, relivePos.y);
		
		m_scores[winner.getGroup()] ++;
		
		JSONObject data = new JSONObject();
		
		data.put("wg", winner.getGroup());
		data.put("wi", winner.getIndex());
		data.put("lg", loser.getGroup());
		data.put("li", loser.getIndex());
		
		foreach(p -> {
			p.send(Cmd.S2C_COLLISION_RESULT, data);
		});
	}
	
	
	private void collisionItems(){
		int len = m_items.size();
		
		foreach(player -> {
			for (Iterator<GameItem> iterator = m_items.iterator(); iterator.hasNext();) {
				GameItem gameItem = iterator.next();
				if(player.tryCollision(gameItem)){
					notifyItemCollision(gameItem);
					iterator.remove();
				}
			}
		});
	}
	
	
	// 道具目前被谁吃了不重要，重要的是道具触发的全局效果
	private void notifyItemCollision(GameItem item){
		JSONObject data = new JSONObject();
//		data.put("t", item.getType());
		data.put("id", item.getId());
		
		foreach(p -> {
			p.send(Cmd.S2C_ITEM_CHANGE, data);
		});
		
		m_state = item.getType();
	}
	
	
	
	
	
	
	
	class Map {
		final public static int TILE_NORMAL = 1;
		final public static int TILE_PHY = 2;
		public int[][] tileData;
		private int m_row;
		private int m_col;
		
		Map(int row, int col, int rate){
			m_row = row;
			m_col = col;
			tileData = new int[row][col];
			
			// 随机一个地图
			for(int i=0; i<row; i++){
				for(int j=0; j<col; j++){
					int r = Mathf.randomInt(0, 10);
					r = r < rate ? TILE_PHY : TILE_NORMAL;
					tileData[i][j] = r;
				}
			}
			
//			tileData = new int[][]{
//					{1,2},
//					{2,1}
//			};
		}
		
		
		public Vector3 getEmptyPos(){
			int rnd = getEmptyPosByInt();
			if(rnd != -1)
				return getVector3ByInt(rnd);
			
			return null;
		}
		
		
		public int getEmptyPosByInt(){
			int rnd, x, y;
			int total = m_row * m_col;
			//1000 次保护
			for (int i = 0; i < 1000 ;) {
				rnd = Mathf.randomInt(0, total);
				
				y = rnd % m_row;
				x = rnd / m_row;
				
				if(tileData[y][x] != TILE_PHY){
					return rnd;
				}
				i++;
			}
			
			Logger.error("can't find rnd pos by int");
			return -1;
		}
		
		
		public Vector3 getVector3ByInt(int i){
			return new Vector3(i / m_row, i % m_row);
		}
		
		
		public JSONArray toJSONArray(){
			JSONArray arr = new JSONArray();
			for (int i = 0; i < tileData.length; i++) {
				JSONArray arr1 = new JSONArray();
				for (int j = 0; j < tileData[i].length; j++) {
					arr1.put(tileData[i][j]);
				}
				arr.put(arr1);
			}
			
			return arr;
		}
	}


	
}
