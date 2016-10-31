package gege.game;

import gege.common.GameSession;
import gege.common.StateData;
import gege.consts.Cmd;
import gege.consts.GameState;
import gege.game.Room.Visitor;
import gege.util.Tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * 避免多线程问题，原则上游戏开始了，不对player进行增删操作
 * 
 * @author vineleven
 *
 */
public class World extends GameEntity {
	
	private static int m_index = 0;
	final public int index;
	
	
	
	
	
	private ArrayList<ArrayList<Player>> m_group = new ArrayList<ArrayList<Player>>(2);;
	
	private Map m_map;
	
	private float m_speed = 1;
	
	private boolean m_bStart = false;
	
	
	
	public World() {
		index = m_index++;
	}
	
	
	public void createWorld(Room room){
		m_group.clear();
		
		int col = 2;
		int row = 2;
		
		// 地图
		m_map = new Map(row, col);
		// 玩家
		int sideCount = room.getSideCount();
		for (int i = 0; i < 2; i++) {
			m_group.add(new ArrayList<Player>(sideCount));
		}
		room.forEach(this::addPlayer);
		room.clear();
		
		// 根据地图随机玩家位置
		HashSet<Integer> record = new HashSet<>();
		int rnd = 0;
		int total = row * col;
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < sideCount;) {
				rnd = Tools.getRandomInt(0, total);
				if(record.contains(rnd))
					continue;
				
				int x = rnd % row;
				int y = rnd / row;
				
				if(m_map.tileData[x][y] != Map.TILE_PHY){
					m_group.get(j).get(i).init(j, x, y, m_speed);
					record.add(rnd);
					i++;
				}
			}
		}
		
		
		// 通知所有玩家
	}
	
	
	private void addPlayer(Visitor v){
		int group = v.getGroup();
		v.getSession().setOnDisconnect(this::onDisconnected);
		int idx = m_group.get(group).size();
		v.getSession().setState(GameState.IN_GAME, new StateData(this.index, group, idx));
		m_group.get(group).add(new Player(v.getSession(), idx));
	}
	
	
	public boolean empty(){
		for (int i = 0; i < m_group.size(); i++) {
			if(m_group.get(i).size() > 0)
				return false;
		}
		
		return true;
	}
	
	
	private void onDisconnected(GameSession session){
	}
	
	
	private void foreach(Consumer<Player> action){
		for (int i = 0; i < m_group.size(); i++) {
			int size = m_group.get(i).size();
			for (int j = 0; j < size; j++) {
				action.accept(m_group.get(i).get(j));
			}
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
		data.put("time", System.currentTimeMillis() + delay);
		
		foreach(p -> {
			data.put("idx", p.getIndex());
			data.put("group", p.getGroup());
			p.getSession().send(Cmd.C2S_START_GAME, data);
		});
	}
	
	
	public void start(){
		int delay = 3000;
		notifyStartInfo(delay);
		m_bStart = true;
		Game.getInstance().callLaterTime(delay, this::onStart);
	}
	
	
	private void onStart(){
		if(!m_bStart)
			return;
		
		
	}
	
	
	
	
	
	
	class Map {
		final public static int TILE_NORMAL = 1;
		final public static int TILE_PHY = 2;
		public int[][] tileData;
		
		Map(int row, int col){
//			tileData = new int[row][col];
//			for(int i=0; i<row; i++){
//				for(int j=0; j<col; j++){
//					int r = Tools.getRandomInt(0, 10);
//					r = r > 7 ? TILE_PHY : TILE_NORMAL;
//					tileData[i][j] = r;
//				}
//			}
			
			tileData = new int[][]{
					{1,2},
					{2,1}
			};
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
