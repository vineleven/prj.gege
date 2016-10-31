package gege.game;

import gege.common.Callback;
import gege.common.EventDispatcher;
import gege.common.EventHandler;
import gege.common.GameEvent;
import gege.common.GameSession;
import gege.common.Request;
import gege.common.SyncQueue;
import gege.common.TickThread;
import gege.consts.Cmd;
import gege.consts.EventId;
import gege.consts.GameState;
import gege.consts.Global;
import gege.game.Room.Visitor;
import gege.util.Logger;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.JSONArray;
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
	
	// 所有房间
	private ArrayList<Room> m_rooms = new ArrayList<>();
	
	// 所有副本
	private ArrayList<World> m_worlds = new ArrayList<>();
	
	// 注册列表
	private LinkedList<LaterCall> m_callLaters = new LinkedList<LaterCall>();
	
	
	
	private Game(){ super( Global.GAME_UPDATE_INTERVAL ); }
	
	
	private void init(){
		registerMsg();
		registerCmd();
	}
	
	
	private void registerMsg(){
		m_global_eventHander.addEventCallback(EventId.GLOBAL_REQUEST, this::onRequest);
		
		m_game_eventHander = new EventHandler(m_dispatcher);
	}
	
	
	private void registerCmd(){
		m_reqListeners.put(Cmd.C2S_TIME, this::reqGetTime);
		m_reqListeners.put(Cmd.C2S_NAME, this::reqSetName);
		m_reqListeners.put(Cmd.C2S_ROOM_CENTER, this::reqRoomCenter);
		m_reqListeners.put(Cmd.C2S_NEW_ROOM, this::reqNewRoom);
		m_reqListeners.put(Cmd.C2S_JOIN_ROOM, this::reqJoinRoom);
		m_reqListeners.put(Cmd.C2S_LEVEL_ROOM, this::reqLeaveRoom);
		m_reqListeners.put(Cmd.C2S_START_GAME, this::reqStartGame);
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
	
	
	public void callLaterTime(int delay, Callback callback){
		m_callLaters.addLast(new LaterCall(delay + System.currentTimeMillis(), callback));
	}
	
	
	private void procLaterCall(){
		long curTime = System.currentTimeMillis();
		
		for (Iterator<LaterCall> iterator = m_callLaters.iterator(); iterator.hasNext();) {
			LaterCall laterCall = iterator.next();
			if(laterCall.time <= curTime){
				iterator.remove();
				laterCall.callback.call();
			}
		}
	}
	
	
	private void updateGameLogic(){
		
	}
	

	@Override
	public void onUpdate() {
		try {
			procRequest();
			procLaterCall();
			updateGameLogic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void pushMsg(GameSession session, String msg){
		JSONObject data = new JSONObject();
		data.put("msg", msg);
		session.send(Cmd.S2C_SHOW_MSG, data);
	}
	
	
	private void reqGetTime(Request req){
		JSONObject data = req.data;
		data.put("sTime", System.currentTimeMillis());
		Logger.debug("" + data.getLong("sTime"));
		req.getSession().send(Cmd.C2S_TIME, data);
	}
	
	
	private void reqSetName(Request req){
		String name = req.data.getString("name");
		req.getSession().setName(new String(name.getBytes(), CharsetUtil.UTF_8));
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
			info.put("sideCount", room.getSideCount());
			
			list.put(info);
		});
		
		/*
		// 测试
		int count = Tools.getRandomInt(0, 10);
		for (int i = 0; i < count; i++) {
			JSONObject info = new JSONObject();
			info.put("name", "room" + i);
			info.put("idx", i);
			info.put("desc", "3/3");
			list.put(info);
		}
		 */
		
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
		int idx;
		// 如果是在房间状态下，则用自己的索引
		if(req.getSession().inState(GameState.IN_ROOM)){
			idx = req.getSession().getStateData().int1;
		} else {
			idx = req.data.getInt("idx");
		}
		
		if(idx > m_rooms.size())
			return;
		
		Room room = m_rooms.get(idx);
		if(room.empty()){
			pushMsg(req.getSession(), "Room is empty, please refresh room center.");
			return;
		}else if(room.full()){
			pushMsg(req.getSession(), "Room is full, please refresh room center.");
			return;
		}
		
		if(group < 0)
			room.join(req.getSession());
		else
			room.join(req.getSession(), group);
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
	
	
	private void reqLeaveRoom(Request req){
		GameSession session = req.getSession();
		if(session.inState(GameState.IN_ROOM)){
			int idx = session.getStateData().int1;
//			if(idx >= m_rooms.size())
//				return;
			
			m_rooms.get(idx).level(session);
		}else if(session.inState(GameState.IN_GAME)){
			pushMsg(session, "game is start.");
		}
	}
	
	
	private void reqStartGame(Request req){
		if(req.getSession().inState(GameState.IN_ROOM)){
			int roomIdx = req.getSession().getStateData().int1;
			Visitor v = m_rooms.get(roomIdx).getVisitor(req.getSession());
			if(!v.isHost()){
				pushMsg(req.getSession(), "only host can start game.");
				return;
			}
			
			World world = null;
			for (int i = 0; i < m_worlds.size(); i++) {
				if(m_worlds.get(i).empty()){
					world = m_worlds.get(i);
					break;
				}
			}
			
			if(world == null)
				world = new World();
			
			world.createWorld(m_rooms.get(roomIdx));
			m_worlds.add(world);
			world.start();
		}
	}
	
	

	
	class LaterCall{
		long time;
		Callback callback;
		LaterCall(long time, Callback callback) {
			this.time = time;
			this.callback = callback;
		}
	}
}
