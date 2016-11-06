package gege.game;

import gege.common.GameSession;
import gege.common.StateData;
import gege.consts.Cmd;
import gege.consts.GameState;
import gege.util.Logger;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;

public class Room {

	private static int room_index = 0;
	
	private String m_name = "007";

	// 单边人数[1-5]
	private int m_sideCount = 0;


	private ArrayList<ArrayList<Visitor>> m_groups = new ArrayList<ArrayList<Visitor>>(2);
//	private ArrayList<Visitor> m_visitors;

	final public int index;

	public Room() {
		index = room_index++;
	}

	public void init(int count) {
		m_sideCount = count;

		if (m_groups != null)
			m_groups.clear();
		
		for (int i = 0; i < 2; i++) {
			m_groups.add(new ArrayList<Visitor>(m_sideCount));
		}
	}

	public boolean empty() {
		for (int i = 0; i < m_groups.size(); i++) {
			if(m_groups.get(i).size() > 0)
				return false;
		}
		
		return true;
	}

	public boolean full() {
		for (int i = 0; i < m_groups.size(); i++) {
			if(m_groups.get(i).size() < m_sideCount)
				return false;
		}
		
		return true;
	}

	public String getName() {
		return m_name;
	}
	
	public int getSideCount() {
		return m_sideCount;
	}

	// 改变
	public void join(GameSession guest, int group) {
		checkAll();
		
		if(group >= m_groups.size()){
//			Logger.error("join: invalid group:" + group);
			return;
		}
		
		if(m_groups.get(group).size() >= m_sideCount){
			Logger.error("room full");
			return;
		}
		
		for (int i = 0; i < m_groups.size(); i++) {
			for (int j = 0; j < m_groups.get(i).size(); j++) {
				if(m_groups.get(i).get(j).equalsSession(guest)){
					m_groups.get(i).get(j).setGroup(group);
					notifyAllPlayer();
					return;
				}
			}
		}

		Visitor v = new Visitor(guest, index);
		v.setGroup(group);
		if (empty()){
			v.setHost();
			// 用房主的名字
			m_name = v.getName();
		}

		m_groups.get(group).add(v);
		
		guest.setState(GameState.IN_ROOM, new StateData(index));
		guest.setOnDisconnect(this::onDisconnect);
		notifyAllPlayer();
	}

	// 新入
	public void join(GameSession session) {
		checkAll();
		
		// 自动往人数少的一方加
		int[] groupCount = new int[2];
		forEach(visitor -> {
			groupCount[visitor.getGroup()]++;
		});
		
		join(session, groupCount[0] > groupCount[1] ? 1 : 0);
	}
	
	
	/**
	 * 人数不够自动用AI补满
	 */
	public void fill(){
		if(empty()){
			Logger.error("can't fill a empty room");
			return;
		}
		
		for (int i = 0; i < m_groups.size(); i++) {
			for (int j = m_groups.get(i).size(); j < m_sideCount; j++) {
				GameSession session = new AIPlayer.AISession();
				session.setName("AI_NO:" + i);
				Visitor v = new Visitor(session, index);
				v.setAI();
				m_groups.get(i).add(v);
				v.setGroup(i);
			}
		}
	}
	
	private void addHost(){
		for (int i = 0; i < m_groups.size(); i++) {
			if(m_groups.get(i).size() > 0){
				m_groups.get(i).get(0).setHost();
				m_name = m_groups.get(i).get(0).getName();
				break;
			}
		}
	}

	public void onDisconnect(GameSession session) {
		checkAll();

		if (empty())
			return;

		boolean hasHost = false;
		out: for (int i = 0; i < m_groups.size(); i++) {
			for (int j = 0; j < m_groups.get(i).size(); j++) {
				if(m_groups.get(i).get(j).isHost()){
					hasHost = true;
					break out;
				}
			}
		}

		if (!hasHost)
			addHost();

		notifyAllPlayer();
	}

	public boolean level(GameSession session) {
		Visitor v = getVisitor(session);
		if (v == null)
			return false;

		session.setState(GameState.IDLE, null);
		session.setOnDisconnect(null);
		
		for (int i = 0; i < m_groups.size(); i++)
			if(m_groups.get(i).remove(v))
				break;

		if (!empty()) {
			if (v.isHost())
				addHost();
			
			notifyAllPlayer();
		}

		return true;
	}

	public void checkAll() {
		int len;
		
		for (int i = 0; i < m_groups.size(); i++) {
			len = m_groups.get(i).size() - 1;
			for (int j = len; j >= 0; j--) {
				if(!m_groups.get(i).get(j).isOnline()){
					m_groups.get(i).remove(j);
				}
			}
		}
	}

	public String getDesc() {
		checkAll();
		
		String str = "";
		for (int i = 0; i < m_groups.size(); i++) {
			if(i == 0)
				str += m_groups.get(i).size();
			else
				str +=("/" + m_groups.get(i).size());
		}

		return str;
	}

	public Visitor getVisitor(GameSession session) {
		for (int i = 0; i < m_groups.size(); i++)
			for (int j = 0; j < m_groups.get(i).size(); j++)
				if(m_groups.get(i).get(j).equalsSession(session))
					return m_groups.get(i).get(j);

		return null;
	}

	public void forEach(Consumer<Visitor> action) {
		checkAll();
		for (int i = 0; i < m_groups.size(); i++) {
			m_groups.get(i).forEach(action);
		}
	}

	
	public void notifyAllPlayer() {
		JSONArray list = new JSONArray();
		forEach(visitor->{
			JSONObject obj = new JSONObject();
			obj.put("name", visitor.getName());
			obj.put("group", visitor.getGroup());
			obj.put("host", visitor.isHost());
			list.put(obj);
		});
		
		JSONObject data = new JSONObject();
		data.put("list", list);
		data.put("idx", index);
		
		forEach(visitor -> {
			visitor.getSession().send(Cmd.S2C_ROOM_INFO, data);
		});
	}

	public void clear() {
		m_groups.clear();
	}
	
	
	

	public class Visitor {

		private GameSession m_session = null;
		private boolean m_bHost = false;
		private boolean m_bAI = false;
		private int m_group = 0;
		private int m_roomIdx = 0;

		public Visitor(GameSession session, int roomIdx) {
			m_session = session;
			m_roomIdx = roomIdx;
		}

		public GameSession getSession() {
			return m_session;
		}

		public int getRoomIdx() {
			return m_roomIdx;
		}

		public boolean isHost() {
			return m_bHost;
		}

		public void setHost() {
			m_bHost = true;
		}
		
		public boolean isAI(){
			return m_bAI;
		}
		
		public void setAI(){
			m_bAI = true;
		}

		public String getName() {
			return m_session.getPlayerName();
		}

		public void setGroup(int group) {
			m_group = group;
		}

		public int getGroup() {
			return m_group;
		}

		public boolean isOnline() {
			return m_session.enabled();
		}

		public boolean equalsSession(Object obj) {
			return m_session == obj;
		}
		
	}
}
