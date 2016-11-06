package gege.net.server;

import gege.game.AIPlayer;
import gege.game.Game;
import gege.game.Player;
import gege.mgr.mgrSession;





public class _Main {
	
	
	public static void main(String[] args) {
		mgrSession.initialize();
		Game.create().start();
		GameServer.create().boot();
	}
	
	
	
	
	
}
