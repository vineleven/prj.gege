package gege.game;

import java.util.ArrayList;

public class Room {
//	private final Player m_host;
	private final ArrayList<Player> m_clients;
	
	
	public Room(Player host, int count) {
//		m_host = host;
		m_clients = new ArrayList<Player>(count);
		m_clients.add(host);
		
		
	}
	
	
	
}
