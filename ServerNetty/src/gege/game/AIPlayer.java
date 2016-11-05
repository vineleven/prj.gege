package gege.game;

import org.json.JSONObject;

import io.netty.channel.Channel;
import gege.common.GameSession;
import gege.consts.GameState;

public class AIPlayer extends Player {

	private World m_world;
	
	public AIPlayer(GameSession session, int index, World world) {
		super(session, index);
		m_world = world;
	}

	
	@Override
	boolean updatePos() {
		if(super.updatePos())
			return true;
		
		
		// 寻路并广播
		if(m_world.inState(getGroup())){
			// 找人追
		} else {
			// 躲人
		}
		
		
		return false;
	}
	
	
	// 模拟GameSession
	public static class AISession extends GameSession {
		public AISession() {
			super(null);
		}
		
		
		@Override
		public boolean inState(GameState state) {
			return true;
		}
		
		
		@Override
		public void send(int cmd, JSONObject data) {
		}
		
		
		@Override
		public boolean enabled() {
			return true;
		}
	}
}
