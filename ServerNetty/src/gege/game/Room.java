package gege.game;

import gege.common.GameSession;
import gege.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;


public class Room extends GameEntity {
	
	private static int room_index = 0;
	
	
	// 匹配(暂未设计)
	final private static int STYLE_MATCHING = 0;
	// 自定义
	final public static int STYLE_CUSTOM = 1;
	
	// 单边人数[1-5]
	private int m_sideCount = 0;
	
	private int m_style = STYLE_CUSTOM;
	
	private ArrayList<Visitor> m_visitors;
	
	
	final public int index;
	
	public Room() {
		index = room_index++;
	}
	
	
	public void init(int style, int count){
		m_style = style;
		m_sideCount = count;
		
		int totalCount = 0;
		if(m_style == STYLE_CUSTOM){
			totalCount = count * 2;
		} else if(m_style == STYLE_MATCHING) {
			Logger.error("暂未开放 匹配模式");
		}
		
		if(m_visitors != null)
			m_visitors.clear();
		
		m_visitors = new ArrayList<Visitor>(totalCount);
	}
	
	
	public boolean empty(){
		return m_visitors.size() == 0;
	}
	
	
	public String getName(){
		if(m_visitors.size() > 0)
			return m_visitors.get(0).getName();
		
		return null;
	}
	
	
	// 改变
	public void join(GameSession guest, int group){
		checkAll();
		for (int i = 0; i < m_visitors.size(); i++) {
			if(m_visitors.get(i).equals(guest)){
				m_visitors.get(i).setGroup(group);
				notifyAllPlayer();
				return;
			}
		}
		
		if(m_visitors.size() < m_sideCount){
			Visitor v = new Visitor(guest);
			v.setGroup(group);
			if(empty())
				v.setHost();
			
			m_visitors.add(v);
			notifyAllPlayer();
		} else
			Logger.error("room full");
	}
	
	
	// 新入
	public void join(GameSession session){
		checkAll();
		int[] groupCount = new int[2];
		Visitor vs;
		for (int i = 0; i < m_visitors.size(); i++) {
			vs = m_visitors.get(i);
			groupCount[vs.getGroup()]++;
		}
		
		join(session, groupCount[0] > groupCount[1] ? 1 : 0);
	}
	
	
	public void level(Visitor visitor){
		m_visitors.remove(visitor);
		if(m_visitors.size() <= 0){
			Game.dispatchGameEvent(null, null);
		} else{
			if(visitor.isHost())
				m_visitors.get(0).setHost();
		}

		notifyAllPlayer();
	}
	
	
	public void checkAll(){
		for (int i = m_visitors.size() - 1; i >= 0; i--) {
			if(!m_visitors.get(i).online()){
				m_visitors.remove(i);
			}
		}
	}
	
	
	public String getDesc(){
		checkAll();
		HashMap<Integer, Integer> mask = new HashMap<>();
		m_visitors.forEach(v->{
			if(mask.containsKey(v.getGroup())){
				mask.put(v.getGroup(), mask.get(v.getGroup()) + 1);
			} else {
				mask.put(v.getGroup(), 1);
			}
		});
		
		StringBuilder sb = new StringBuilder();
		mask.forEach((i1, i2)->{
			if(sb.length() == 0)
				sb.append(i2.toString());
			else
				sb.append("/" + i2.toString());
		});
		
		return sb.toString();
	}
	
	
	public void forEach(Consumer<Visitor> action){
		checkAll();
		m_visitors.forEach(action);
	}
	
	
	public void notifyAllPlayer(){
		for (int i = 0; i < m_visitors.size(); i++) {
			Game.getInstance().pushRoomInfo(m_visitors.get(i).getSession(), index);
		}
	}
	
	
	
	
	
	public class Visitor extends GameEntity {
		
		private GameSession m_session = null;
		private boolean m_bHost = false;
		private int m_group = 0;
		
		public Visitor(GameSession session) {
			m_session = session;
		}
		
		
		public GameSession getSession(){
			return m_session;
		}
		
		
		public boolean isHost(){
			return m_bHost;
		}
		
		
		public void setHost(){
			m_bHost = true;
		}
		
		
		public String getName(){
			return m_session.getPlayerName();
		}
		
		
		public void setGroup(int group){
			m_group = group;
		}
		
		
		public int getGroup(){
			return m_group;
		}
		
		
		public boolean online(){
			return m_session.enabled();
		}
		
		
		@Override
		public boolean equals(Object obj) {
			return m_session == obj;
		}
	}
}
