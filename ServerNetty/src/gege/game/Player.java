package gege.game;

import gege.common.GameSession;
import gege.consts.EventId;
import gege.util.Logger;

import org.json.JSONObject;

public class Player extends GameEntity{
	
	final public static int STATE_NONE = 0;
	final public static int STATE_NORMAL = 1;
	final public static int STATE_DEAD = 2;
	final public static int STATE_INVINCIBLE = 3;
	final public static int STATE_OFFLINE = 4;
	
	
	private GameSession m_session;
	
	final private int m_index;
	
	private int m_group;
	
	float m_speed = 0;
	
	PosInfo m_nextPos;
	
	// 累计误差
	private float m_cumulativeErrorValue = 0;
	
	// 验证标记
	private boolean m_bVerify = false;
	
	// 复活时间
	long m_reliveTime = 0;
	
	boolean m_hasAI = false;
	
	
	public Player(GameSession session, int index) {
		m_session = session;
		m_index = index;
	}
	
	
	public void init(int group, float startX, float startY, float speed){
		m_group = group;
		m_nextPos = new PosInfo(startX, startY, startX, startY, 1, 0);
		m_speed = speed;
		
		setPosition(startX, startY);
	}
	
	
	public GameSession getSession(){
		return m_session;
	}
	
	
	public void send(int cmd, JSONObject data){
		if(m_session != null && !isDisposed())
			m_session.send(cmd, data);
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
	
	
	public boolean equalsPlayer(Player p) {
		return p.getGroup() == getGroup() && p.getIndex() == getIndex();
	}
	
	
	public void goGoGo(){
		if(!inState(STATE_OFFLINE))
			setNextState(STATE_NORMAL);
	}
	
	
	public void leave(){
		setNextState(STATE_OFFLINE);
	}
	
	
	public boolean isOnline(){
		return m_session.enabled();
	}
	
	
	public boolean isNormal(){
		return inState(STATE_NORMAL);
	}
	
	
	public boolean isDead(){
		return inState(STATE_DEAD);
	}
	
	
	public JSONObject getStartInfo(){
		JSONObject info = new JSONObject();
		info.put("x", m_nextPos.m_originX);
		info.put("y", m_nextPos.m_originY);
//		info.put("s", m_speed);
		info.put("g", m_group);
		info.put("i", m_index);
		info.put("n", getName());
		
		return info;
	}
	
	
	void setNextPos(float nextX, float nextY, long arriveTime){
		int delta = (int) (arriveTime - Game.worldTime);
		m_nextPos.reset(x, y, nextX, nextY, delta, arriveTime);
	}
	
	
	boolean tryMove(){
//		if(m_group == 0)
//			Logger.debug("--- debugPos x:" + x + " y:" + y + " nx:" + m_nextPos.m_nextX + " ny:" + m_nextPos.m_nextY + " delta:" + m_nextPos.m_delta);
		
		if(!m_nextPos.isArrived(x, y)){
			float t = (m_nextPos.m_arriveTime - Game.worldTime) / m_nextPos.m_delta;
			t = 1 - t;
			x = m_nextPos.lerpX(t);
			y = m_nextPos.lerpY(t);
			return true;
		}
		
		return false;
	}
	
	
	@Override
	public void onUpdate() {
		switch (m_state) {
		case STATE_NONE:
			break;
		case STATE_NORMAL:
			onUpdateNormal();
			break;
		case STATE_DEAD:
			onUpdateDead();
			break;
		case STATE_INVINCIBLE:
			onUpdateInvincible();
			break;
		case  STATE_OFFLINE:
			break;
		default:
			Logger.error("can't find player state:" + m_state);
			break;
		}
	}
	
	
	private void onUpdateNormal(){
		tryMove();
	}
	
	
	private void onUpdateDead(){
		if(m_reliveTime > Game.worldTime)
			return;
		
		relive();
	}
	
	
	private void onUpdateInvincible(){
		tryMove();
	}
	
	
	/**
	 * @param nextX
	 * @param nextY
	 * @param arriveTime
	 * @param verify 验证包
	 * @return
	 */
	public boolean tryMove(float nextX, float nextY, long arriveTime, boolean verify){
		if(arriveTime <= m_nextPos.m_arriveTime){
			// 非法数据包，直接丢弃
			return false;
		}
		

//		float time = Mathf.distance(nextX, nextY, x, y) / m_speed;
//		long sArriveTime = (long) (Game.getInstance().getCurTime() + time);
		// 标准时间误差
//		long delta = sArriveTime - arriveTime;
//		m_cumulativeErrorValue += delta;
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
		
//		float curSpeed = Mathf.distance(nextX, nextY, x, y) / (arriveTime - m_nextPos.m_arriveTime);
		// 3f/1000 的速度，40秒m_cumulativeErrorValue>15就算不正常了
//		m_cumulativeErrorValue += (curSpeed - m_speed);
//		Logger.debug("cur Speed:" + (curSpeed * 1000) + " cv:" + m_cumulativeErrorValue);
		
		
//		long delta = Game.getInstance().getCurTime() - arriveTime;
//		if(delta <= 200){
//			// 非法数据，暂定最大容忍200ms误差
//			// 延迟过大也可能造成时间差过大
//			// 这里可以反复找一个最小值，来校对时间误差
//			// 或者强制延迟客户端进行修正(TODO)
//		}
		
		if(arriveTime - m_nextPos.m_arriveTime > 999){
			// 超时重连或者玩家长时间未操作
		}
		
		
		// 暂时无条件信任客户端
		setNextPos(nextX, nextY, arriveTime);
		
		
		return true;
	}
	
	
	/**
	 * @param reliveX
	 * @param reliveY 复活坐标
	 */
	public void dead(float reliveX, float reliveY){
		// 3秒后复活
		m_reliveTime = Game.worldTime + 3000;
		m_nextPos.reset(reliveX, reliveY, reliveX, reliveY, 1, 0);
		setPosition(reliveX, reliveY);
		setNextState(STATE_DEAD);
	}
	
	
	private void relive(){
		Game.dispatchGameEvent(EventId.PLAYER_RELIVE, this);
		setNextState(STATE_NORMAL);
	}
	
}
