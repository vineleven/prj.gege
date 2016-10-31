package gege.net.server;

import org.json.JSONObject;

import gege.game.Game;
import gege.mgr.mgrSession;





public class _Main {
	public static void main(String[] args) {
		
		mgrSession.initialize();
		Game.create().start();
		GameServer.create().boot();
		
		JSONObject obj = new JSONObject();
		obj.put("a", "中文");
		
		String str = obj.toString();
		JSONObject obj1 = new JSONObject(str);
		
		System.out.println(str);
		System.out.println(obj1.getString("a"));
	}
	
}
