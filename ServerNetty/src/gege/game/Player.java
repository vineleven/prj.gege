package gege.game;

import gege.common.GameSession;
import gege.util.Mathf;

import org.json.JSONObject;

public class Player extends GameEntity{
	
	private GameSession m_session;
	
	final private int m_index;
	
	private int m_group;
	
	// 起始点
	private float m_originX;
	private float m_originY;
	
	private float m_speed = 3f / 1000;
	
	// 下个点
	private float m_nextX;
	private float m_nextY;
	
	
	private long m_arriveTime;
	
	// 累计误差
	private float m_cumulativeErrorValue = 0;
	
	// 验证标记
	private boolean m_bVerify = false;
	
	
	
	public Player(GameSession session, int index) {
		m_session = session;
		m_index = index;
	}
	
	
	public GameSession getSession(){
		return m_session;
	}
	
	
	public int getIndex(){
		return m_index;
	}
	
	
	public int getGroup(){
		return m_group;
	}
	
	
	public String getName(){
		return m_session.getPlayerName();
	}
	
	
	public void init(int group, int startCol, int startRow, float speed){
		m_group = group;
		m_originX = startCol;
		m_originY = startRow;
		m_speed = speed;
		
		setPosition(startCol, startRow);
	}

	
	public JSONObject getStartInfo(){
		JSONObject info = new JSONObject();
		info.put("x", m_originX);
		info.put("y", m_originY);
		info.put("s", m_speed);
		info.put("g", m_group);
		info.put("i", m_index);
		
		return info;
	}
	
	
	private void setNextPos(float nextX, float nextY, long arriveTime){
		m_nextX = nextX;
		m_nextY = nextY;
		m_arriveTime = arriveTime;
	}
	
	
	@Override
	public void onUpdate() {
		x = Mathf.lerp(m_originX, to, t)
	}
	
	
	/**
	 * @param nextX
	 * @param nextY
	 * @param arriveTime
	 * @param verify 验证包
	 * @return
	 */
	public boolean tryMove(float nextX, float nextY, long arriveTime, boolean verify){
		if(arriveTime <= m_arriveTime){
			// 非法数据包，直接丢弃
			return false;
		}
		
		
		float time = Mathf.distance(nextX, nextY, x, y) / m_speed;
		long sArriveTime = (long) (Game.getInstance().getCurTime() + time);

		// 标准时间误差
		long delta = sArriveTime - arriveTime;
		m_cumulativeErrorValue += delta;
		if(m_cumulativeErrorValue > 3000){
			// 误差过大，请求校对
		}
		
		// 验证
		if(m_bVerify){
			if(verify){
			} else {
				// 继续验证
			}
		}

		
//		long delta = Game.getInstance().getCurTime() - arriveTime;
//		if(delta <= 200){
//			// 非法数据，暂定最大容忍200ms误差
//			// 延迟过大也可能造成时间差过大
//			// 这里可以反复找一个最小值，来校对时间误差
//			// 或者强制延迟客户端进行修正(TODO)
//		}
		
		if(arriveTime - m_arriveTime > 999){
			// 超时重连或者玩家长时间未操作
		}
		
		
		// 暂时无条件信任客户端
		setNextPos(nextX, nextY, arriveTime);
		
		
		return false;
	}
	
}
