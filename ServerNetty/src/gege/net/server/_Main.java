package gege.net.server;

import org.json.JSONObject;

import gege.game.Game;
import gege.mgr.mgrSession;





public class _Main {
	public static void main(String[] args) {
//		
		mgrSession.initialize();
		Game.create().start();
		GameServer.create().boot();
		
//		JSONObject obj = new JSONObject();
//		float f = 1 / 3f;
//		obj.put("a", f);
//		
//		String str = obj.toString();
//		JSONObject obj1 = new JSONObject(str);
//		
//		System.out.println(str);
//		double d = obj1.getDouble("a");
//		f = (float)d;
//		System.out.println(d);
//		System.out.println(f);
	}
	
}
