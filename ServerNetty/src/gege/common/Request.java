package gege.common;

import gege.consts.Global;

import org.json.JSONObject;

public class Request extends JSONObject {

	private GameSession m_session;
	public int cmd;
	public JSONObject data;
	
	public Request(GameSession seesion ,String msg) {
		super(msg);
		cmd = getInt(Global.MSG_KEY_CMD);
		data = getJSONObject(Global.MSG_KEY_DATA);
		m_session = seesion;
	}
	
	
	public GameSession getSession(){
		return m_session;
	}
}
