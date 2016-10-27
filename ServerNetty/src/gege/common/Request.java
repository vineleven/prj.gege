package gege.common;

import org.json.JSONObject;

public class Request extends JSONObject {

	private GameSession m_session;
	
	public Request(GameSession seesion ,String msg) {
		super(msg);
		m_session = seesion;
	}
	
	
	public GameSession getSession(){
		return m_session;
	}
}
