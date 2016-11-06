package gege.game;

import java.util.ArrayList;

import gege.common.GameSession;
import gege.common.Queue;
import gege.consts.Cmd;
import gege.consts.GameState;
import gege.exception.GameException;
import gege.util.Logger;
import gege.util.Mathf;

import org.json.JSONObject;

public class AIPlayer extends Player {

	private World m_world;
	
	private long m_changeDirTime = 0;
	
	private Queue<Vector3> m_paths = new Queue<>();
	
	private int m_dir1 = 0;
	private int m_dir2 = 0;
	
	
	public AIPlayer(GameSession session, int index, World world) {
		super(session, index);
		m_world = world;
		m_hasAI = true;
	}

	
	@Override
	boolean tryMove() {
		if(super.tryMove()){
			// 预测下一帧
			float t = (m_nextPos.m_arriveTime - Game.worldTime - Game.deltaTime) / m_nextPos.m_delta;
			t = 1 - t;
			float nextX = m_nextPos.lerpX(t);
			float nextY = m_nextPos.lerpY(t);
			if(!m_nextPos.isArrived(nextX, nextY))
				return true;
		}
		
		if(m_changeDirTime <= Game.worldTime)
			findNextPath();
		
		gotoNext();
		
		return false;
	}
	
	
	// 避免每次都new一个
	private JSONObject posInfo = null;
	void gotoNext(){
		if(m_paths.size() == 0){
			// 继续当前方向
			m_world.getMap().findPath(x,  y, m_dir1, GameMap.DIR_NONE, m_paths);
			if(m_paths.size() == 0){
				if(m_dir2 != GameMap.DIR_NONE){
//					m_dir1 = m_dir2;
//					m_dir2 = GameMap.DIR_NONE;
					m_world.getMap().findPath(x,  y, m_dir2, GameMap.DIR_NONE, m_paths);
					if(m_paths.size() == 0)
						findNextPath();
				} else {
					findNextPath();
				}
			}
		}
		
		Vector3 next = m_paths.dequeue();
		float time = Mathf.distance(x, y, next.x, next.y) / m_speed;
		setNextPos(next.x, next.y, Game.worldTime + (int)time);
		// 广播
		if(posInfo == null){
			posInfo = new JSONObject();
			posInfo.put("g", getGroup());
			posInfo.put("i", getIndex());
		}
		
		posInfo.put("t", m_nextPos.m_arriveTime);
		posInfo.put("x", next.x);
		posInfo.put("y", next.y);
		
		m_world.foreach(player->{
			player.send(Cmd.S2C_PLAYER_POS, posInfo);
		});
	}
	
	
	// 需要保证一定能找到一个点
	private void findNextPath(){
		if(m_world.inState(getGroup())){
			// 找人追
			ArrayList<Player> group = m_world.getGroup(1 - getGroup());
			// 这里看是追一个人，还是追最近的人
			// 暂时随机找一个
			int[] order = Mathf.randomArray(group.size());
			Player nearPlayer = null;
			for (int i = 0; i < order.length; i++) {
				nearPlayer = group.get(i);
				if(nearPlayer.isNormal())
					break;
			}
			
			if(nearPlayer.isNormal()){
				setDir(nearPlayer.x - x, nearPlayer.y - y);
			}
			
		} else {
			// 躲人(不知道如何躲) 去吃自己颜色的球
			GameItem selfItem = null;
			for (GameItem item : m_world.getItems()) {
				if(item.getType() == getGroup()){
					selfItem = item;
					break;
				}
			}
			
			if(selfItem != null){
				setDir(selfItem.x - x,  selfItem.y - y);
			} else {
				Vector3 emptyPos = m_world.getMap().getEmptyPos();
				setDir(emptyPos.x - x,  emptyPos.y - y);
			}
		}
		
		m_world.getMap().findPath(x,  y, m_dir1, GameMap.DIR_NONE, m_paths);
		if(m_paths.size() == 0){
			m_world.getMap().findPath(x,  y, m_dir2, GameMap.DIR_NONE, m_paths);
			if(m_paths.size() == 0){
				m_dir2 = m_dir2 + 2 > 4 ? m_dir2 - 2 : m_dir2 + 2;
				m_world.getMap().findPath(x,  y, m_dir2, GameMap.DIR_NONE, m_paths);
				if(m_paths.size() == 0){
					m_dir1 = m_dir1 + 2 > 4 ? m_dir1 - 2 : m_dir1 + 2;
					m_world.getMap().findPath(x,  y, m_dir1, GameMap.DIR_NONE, m_paths);
					Logger.error("this error because of map has U way");
					if(m_paths.size() == 0)
						throw new GameException("can't find way to go, x:" + x + " y:" + y);
				} else {
					int temp = m_dir1;
					m_dir1 = m_dir2;
					m_dir2 = temp + 2 > 4 ? temp - 2 : temp + 2;
				}
			} else {
				int temp = m_dir1;
				m_dir1 = m_dir2;
				m_dir2 = temp + 2 > 4 ? temp - 2 : temp + 2;
			}
		}
		m_changeDirTime = Game.worldTime + 4000;
	}
	
	@Override
	public void dead(float reliveX, float reliveY) {
		super.dead(reliveX, reliveY);
		m_paths.clear();
	}
	
	
	/**
	 * @param dirX 方向向量
	 * @param dirY
	 */
    public void setDir(float dirX, float dirY)
    {
        int dir1, dir2;
        if (dirY > 0)
            dir1 = GameMap.DIR_UP;
        else
            dir1 = GameMap.DIR_DOWN;

        if (dirX > 0)
            dir2 = GameMap.DIR_RIGHT;
        else
            dir2 = GameMap.DIR_LEFT;

        if (Math.abs(dirX) > Math.abs(dirY))
        {
            int temp = dir1;
            dir1 = dir2;
            dir2 = temp;
        }

        m_dir1 = dir1;
        m_dir2 = dir2;
    }
    
    
    
    
    
    
    
    
    
	
	
	// 模拟GameSession
	public static class AISession extends GameSession {
		public AISession() {super(null);}
		@Override
		public boolean inState(GameState state) {return true;}
		@Override
		public void send(int cmd, JSONObject data) {}
		@Override
		public boolean enabled() {return true;}
	}
}
