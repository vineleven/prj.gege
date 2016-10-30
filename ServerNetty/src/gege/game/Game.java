package gege.game;

import gege.common.GameEvent;
import gege.common.EventDispatcher;
import gege.common.EventHandler;
import gege.common.GameSession;
import gege.common.Request;
import gege.common.SyncQueue;
import gege.common.TickThread;
import gege.consts.Cmd;
import gege.consts.EventId;
import gege.consts.GameState;
import gege.consts.Global;
import gege.util.Logger;
import gege.util.Tools;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 
 * Game内只有Room的集合
 * @author vineleven
 *
 */
public class Game extends TickThread {

	private static Game inst = null;
	
	// 将游戏和全局消息分开
	private static EventDispatcher m_dispatcher = new EventDispatcher();
	
	
	public synchronized static Game create(){
		if( inst == null ){
			inst = new Game();
			inst.init();
		}

		return inst;
	}
	
	
	public static Game getInstance() { return inst; }
	
	
	public static void dispatchGameEvent(EventId eid, Object data){
		m_dispatcher.dispatchEvent(eid, data);
	}
	
	
	
	
	
	
	
	
	interface RequestListener{ void onRequest(Request req);}
	
	// 请求监听
	public HashMap<Integer, RequestListener> m_reqListeners = new HashMap<>();
	
	// 请求列表
	public SyncQueue<Request> m_requestQueue = new SyncQueue<>( Global.GAME_REQUEST_QUEUE_MAX_SIZE );
	
	// 全局消息
	private EventHandler m_global_eventHander = new EventHandler();
	
	// 游戏内消息
	private EventHandler m_game_eventHander = new EventHandler();
	
	// 注册列表
	
	private ArrayList<Room> m_rooms = new ArrayList<>();
	
	
	
	private Game(){ super( Global.GAME_UPDATE_INTERVAL ); }
	
	
	private void init(){
		registerMsg();
		registerCmd();
	}
	
	
	private void registerMsg(){
		m_global_eventHander.addEventCallback(EventId.GLOBAL_REQUEST, this::onRequest);
		
		m_game_eventHander = new EventHandler(m_dispatcher);
		m_game_eventHander.addEventCallback(EventId.GAME_ROOM_EMPTY, this::onRoomEmpty);
	}
	
	
	private void registerCmd(){
		m_reqListeners.put(Cmd.C2S_TIME, this::reqGetTime);
		m_reqListeners.put(Cmd.C2S_NAME, this::reqSetName);
		m_reqListeners.put(Cmd.C2S_ROOM_CENTER, this::reqRoomCenter);
		m_reqListeners.put(Cmd.C2S_NEW_ROOM, this::reqNewRoom);
		m_reqListeners.put(Cmd.C2S_JOIN_ROOM, this::reqJoinRoom);
	}
	
	
	@Override
	public synchronized void start() {
		super.start();
		m_global_eventHander.startProcMsg();
		m_game_eventHander.startProcMsg();
	}
	
	
	public void onRequest(GameEvent e){
		Request req = (Request) e.getData();
		m_requestQueue.enqueue(req);
	}
	
	
	public void procRequest(){
		// 理论需要处理所有游戏相关的请求
		Request req;
		int count = 0;
		try{
			RequestListener l;
			while( count < Global.GAME_MAX_PROCESS_REQUEST && ( req = m_requestQueue.dequeueImm() ) != null ){
				l = m_reqListeners.get(req.cmd);
				if(l != null)
					l.onRequest(req);
				count++;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取即时时间(秒)，游戏逻辑不得使用当前方法
	 */
	public long getCurTime(){ return getLastTickTime() / 1000; }
	
	
	private void updateGameLogic(){
	}
	

	@Override
	public void onUpdate() {
		try {
			procRequest();
			updateGameLogic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void reqGetTime(Request req){
		JSONObject data = req.data;
		data.put("sTime", System.currentTimeMillis());
		Logger.debug("" + data.getLong("sTime"));
		req.getSession().send(Cmd.C2S_TIME, data);
	}
	
	
	private void reqSetName(Request req){
		String name = req.data.getString("name");
		req.getSession().setName(name);
	}
	
	
	private void reqRoomCenter(Request req){
		JSONObject data = new JSONObject();
		JSONArray list = new JSONArray();
		m_rooms.forEach(room -> {
			if(room.empty())
				return;
			JSONObject info = new JSONObject();
			info.put("name", room.getName());
			info.put("idx", room.index);
			info.put("desc", room.getDesc());
			
			list.put(info);
		});
		
		// 测试
		int count = Tools.getRandomInt(0, 10);
		for (int i = 0; i < count; i++) {
			JSONObject info = new JSONObject();
			info.put("name", "room" + i);
			info.put("idx", i);
			info.put("desc", "3/3");
			list.put(info);
		}
		
		data.put("list", list);
		req.getSession().send(Cmd.C2S_ROOM_CENTER, data);
	}
	
	
	private void reqNewRoom(Request req){
		if(!req.getSession().inState(GameState.IDLE))
			return;
		
		int count = req.data.getInt("sideCount");
		if(count < 1)
			return;
		
//		int style = req.data.getInt("style");
		
		Room room = null;
		for (int i = 0; i < m_rooms.size(); i++) {
			if(m_rooms.get(i).empty()){
				room = m_rooms.get(i);
				break;
			}
		}
		
		if(room == null){
			room = new Room();
			m_rooms.add(room);
		}
		
		room.init(Room.STYLE_CUSTOM, count);
		room.join(req.getSession());
		
//		rspRoomInfo(req.getSession(), room.index);
	}
	
	
	private void reqJoinRoom(Request req){
		int group = req.data.getInt("group");
		int idx = req.data.getInt("idx");
		
		if(idx > m_rooms.size())
			return;
		
		if(group < 0)
			m_rooms.get(idx).join(req.getSession());
		else
			m_rooms.get(idx).join(req.getSession(), group);
	}
	
	
	public void pushRoomInfo(GameSession session, int idx){
		if(idx >= m_rooms.size()){
			Logger.error("rspRoomInfo can't find room index:" + idx);
			return;
		}
		Room room = m_rooms.get(idx);
		JSONArray list = new JSONArray();
		room.forEach(visitor->{
			JSONObject obj = new JSONObject();
			obj.put("name", visitor.getName());
			obj.put("group", visitor.getGroup());
			obj.put("host", visitor.isHost());
			list.put(obj);
		});
		
		JSONObject data = new JSONObject();
		data.put("list", list);
		data.put("idx", idx);
		
		session.send(Cmd.S2C_ROOM_INFO, data);
	}
	
	
	private void onRoomEmpty(GameEvent e){
		
	}
	
	
	

}
