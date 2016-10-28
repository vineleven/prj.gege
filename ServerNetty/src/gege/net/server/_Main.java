package gege.net.server;

import gege.game.Game;
import gege.mgr.mgrSession;




public class _Main {
	public static void main(String[] args) {
		
//		mgrProto.registerAllProto();
//		mgrCfg.initialize();
//		mgrMySQL.initialize();
//		mgrShare.initialize();

//		mgrControl.initialize();
		
		

		
//		mgrControl.watch();
//		mgrControl.start();
		
		
		mgrSession.initialize();
		Game.create().start();
		GameServer.create().boot();
	}
	
}
