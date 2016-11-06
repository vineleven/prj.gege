package gege.net.server;

import gege.game.Game;
import gege.mgr.mgrSession;





public class _Main {
	
	
	public static void main(String[] args) {
		mgrSession.initialize();
		Game.create().start();
		GameServer.create().boot();
	}
	
	
	
	
	
}
