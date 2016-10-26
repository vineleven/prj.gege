package gege.game;

import gege.data.Config;
import gege.game.GameServer.GameSession;
import gege.util.SyncQueue;
import gege.util.TickThread;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;


public class Game extends TickThread{
	private Hashtable<String, Player> m_playerTable = new Hashtable<String, Player>();
	
	private SyncQueue<JSONObject> m_reqQueue = new SyncQueue<JSONObject>();
	
	
	
	public Game() {
		super(Config.GAME_SRERVER_UPDATE_INTERVAL);
	}
	
	
	public void login(GameSession session){
		session.setCloseCallback(()->{logout(session);});
		Player player = new Player(session);
		m_playerTable.put(session.getRemoteAddr(), player);
	}
	
	
	public void logout(GameSession session){
		m_playerTable.remove(session.getRemoteAddr());
	}
	
	
	public void request(JSONObject req){
		try {
			String cmd = req.getString("cmd");
			if(!cmd.isEmpty())
				m_reqQueue.enqueue(req);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onUpdate() {
		
	}
}
