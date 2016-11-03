package gege.game;

import gege.common.GameSession;

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
		
		
		
		
		return false;
	}
}
