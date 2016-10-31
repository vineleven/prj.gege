package gege.game;

import gege.common.GameSession;

import org.json.JSONObject;

public class Player extends GameEntity{
	
	private GameSession m_session;
	
	final private int m_index;
	
	
	private float m_startX;
	private float m_startY;
	private float m_speed;
	
	
	private float m_nextX;
	private float m_nextY;
	
	private int m_group;
	
	
	
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
		m_startX = startCol;
		m_startY = startRow;
		m_speed = speed;
	}
	
	
	public JSONObject getStartInfo(){
		JSONObject info = new JSONObject();
		info.put("x", m_startX);
		info.put("y", m_startY);
		info.put("s", m_speed);
		info.put("g", m_group);
		info.put("i", m_index);
		
		return info;
	}
	
	
	
	
	
	public void update(float dt){
		
	}
	
	
}
