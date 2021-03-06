package gege.game;

import gege.common.Callback;
import gege.common.EventDispatcher;
import gege.common.EventHandler;
import gege.common.GameEvent;
import gege.common.GameSession;
import gege.common.Request;
import gege.common.StateData;
import gege.common.SyncQueue;
import gege.common.TickThread;
import gege.consts.Cmd;
import gege.consts.ErrorCode;
import gege.consts.EventId;
import gege.consts.GameState;
import gege.consts.Global;
import gege.game.Room.Visitor;
import gege.util.Logger;
import io.netty.util.CharsetUtil;

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
	
	
	
	
	
	
	public static long worldTime = 0;
	public static long deltaTime = 0;
	
	interface RequestListener{ void onRequest(Request req);}
	
	// 请求监听
	public HashMap<Integer, RequestListener> m_reqListeners = new HashMap<>();
	
	// 请求列表
	public SyncQueue<Request> m_requestQueue = new SyncQueue<>( Global.GAME_REQUEST_QUEUE_MAX_SIZE );
	
	// 全局消息
	private EventHandler m_global_eventHander = new EventHandler();
	
	// 游戏内消息
	private EventHandler m_game_eventHander = new EventHandler(m_dispatcher);
	
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
		m_game_eventHander.addEventCallback(EventId.PLAYER_RELIVE, this::onPlayerRelive);
	}
	
	
	private void registerCmd(){
		m_reqListeners.put(Cmd.C2S_TIME, this::reqGetTime);
		m_reqListeners.put(Cmd.C2S_NAME, this::reqSetName);
		m_reqListeners.put(Cmd.C2S_ROOM_CENTER, this::reqRoomCenter);
		m_reqListeners.put(Cmd.C2S_NEW_ROOM, this::reqNewRoom);
		m_reqListeners.put(Cmd.C2S_JOIN_ROOM, this::reqJoinRoom);
		m_reqListeners.put(Cmd.C2S_LEAVE_ROOM, this::reqLeaveRoom);
		m_reqListeners.put(Cmd.C2S_START_GAME, this::reqStartGame);
		m_reqListeners.put(Cmd.C2S_PLAYER_POS, this::reqPlayerPos);
		m_reqListeners.put(Cmd.C2S_LEAVE_GAME, this::reqLeaveGame);
		
		m_reqListeners.put(Cmd.C2S_GM, this::reqGm);
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
			
			if(m_requestQueue.size() > 0)
				Logger.warn("request is too many.");
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取即时时间(毫秒)，游戏逻辑不得使用当前方法
	 */
	public long getCurTime(){ return getLastTickTime(); }
	
	// 这里假定是稳定的
	public long getDeltaTime(){
		return getInterval();
	}
	
	
	public void callLaterTime(int delay, Callback callback){
		m_callLaters.addLast(new LaterCall(delay + getCurTime(), callback));
	}
	
	
	private void procLaterCall(){
		long curTime = getCurTime();
		
		for (Iterator<LaterCall> iterator = m_callLaters.iterator(); iterator.hasNext();) {
			LaterCall laterCall = iterator.next();
			if(laterCall.time <= curTime){
				iterator.remove();
				laterCall.callback.call();
			}
		}
	}
	
	
	private void updateGameLogic(){
		int len = m_worlds.size();
		for (int i = 0; i < len; i++) {
			m_worlds.get(i).update();
		}
	}
	

	@Override
	public void onUpdate() {
		try {
			deltaTime = getCurTime() - worldTime;
			worldTime = getCurTime();
			procRequest();
			procLaterCall();
			updateGameLogic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void pushMsg(GameSession session, String msg, int errorCode){
		JSONObject data = new JSONObject();
		data.put("msg", msg);
		data.put("code", errorCode);
		session.send(Cmd.S2C_SHOW_MSG, data);
	}
	
	
	private void reqGm(Request req){
		String gmCode = req.data.getString("code");
		if("GameOver".equals(gmCode)){
			if(req.getSession().inState(GameState.IN_GAME)){
				World world = m_worlds.get(req.getSession().getStateData().int1);
				world.gm2GameOver();
			}
		}
	}
	
	
	private void reqGetTime(Request req){
		JSONObject data = req.data;
		data.put("sTime", getCurTime());
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
		if(!req.getSession().inState(GameState.IDLE)){
			// 可能断线重连或者重启游戏
			pushMsg(req.getSession(), "you are in game or in room.", ErrorCode.NOT_IN_IDLE);
			return;
		}
		
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
		
		room.init(count);
		room.join(req.getSession());
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
			pushMsg(req.getSession(), "Room is empty, please refresh room center.", ErrorCode.NONE);
			return;
		}else if(room.full()){
			pushMsg(req.getSession(), "Room is full, please refresh room center.", ErrorCode.NONE);
			return;
		}
		
		if(group < 0)
			room.join(req.getSession());
		else
			room.join(req.getSession(), group);
	}
	
	
	private void reqLeaveRoom(Request req){
		GameSession session = req.getSession();
		if(session.inState(GameState.IN_ROOM)){
			int idx = session.getStateData().int1;
//			if(idx >= m_rooms.size())
//				return;
			
			m_rooms.get(idx).level(session);
		}else if(session.inState(GameState.IN_GAME)){
			pushMsg(session, "game is start.", ErrorCode.NONE);
		}
	}
	
	
	// TODO: 暂时没有人数验证
	private void reqStartGame(Request req){
		if(req.getSession().inState(GameState.IN_ROOM)){
			int roomIdx = req.getSession().getStateData().int1;
			Visitor v = m_rooms.get(roomIdx).getVisitor(req.getSession());
			if(!v.isHost()){
				pushMsg(req.getSession(), "only host can start game.", ErrorCode.NONE);
				return;
			}
			
			m_rooms.get(roomIdx).fill();
			
			World world = null;
			for (int i = 0; i < m_worlds.size(); i++) {
				if(m_worlds.get(i).checkEmpty()){
					world = m_worlds.get(i);
					break;
				}
			}
			
			if(world == null)
				world = new World();
			
			world.createWorld(m_rooms.get(roomIdx));
			m_worlds.add(world);
			world.start();
		} else {
			pushMsg(req.getSession(), "you are not in room.", ErrorCode.NOT_IN_ROOM);
		}
	}
	
	
	private void reqPlayerPos(Request req){
		if(!req.getSession().inState(GameState.IN_GAME)){
			pushMsg(req.getSession(), "you are not in game.", ErrorCode.NOT_IN_GAME);
			return;
		}
		
		StateData sData = req.getSession().getStateData();
		
		World world = m_worlds.get(sData.int1);
		if(!world.isStart()){
			pushMsg(req.getSession(), "game not start.", ErrorCode.NONE);
			return;
		}
		
		Player player = world.getPlayer(sData.int2, sData.int3);
		
		JSONObject data = req.data;
		long arriveTime = data.getLong("t");
		float x = (float) data.getDouble("x");
		float y = (float) data.getDouble("y");
		
//		Logger.debug("--->req pos x:" + x + " y:" + y);
		
		// 直接这个加工这个数据广播，节省一点
		data.put("g", player.getGroup());
		data.put("i", player.getIndex());
		
		if(player.tryMove(x, y, arriveTime, false)){
			world.foreach(p -> {
				if(!p.equalsPlayer(player)){
					p.send(Cmd.S2C_PLAYER_POS, data);
				}
			});
		}
		
//		Logger.debug("---->" + req.data.toString());
	}
	
	
	private void reqLeaveGame(Request req){
		if(req.getSession().inState(GameState.IN_GAME)){
			StateData sData = req.getSession().getStateData();
			World world = m_worlds.get(sData.int1);
			world.getPlayer(sData.int2, sData.int3).dispose();
			
			req.getSession().setState(GameState.IDLE, null);
			req.getSession().setOnDisconnect(null);
			
			JSONObject data = new JSONObject();
			data.put("g", sData.int2);
			data.put("i", sData.int3);
			world.foreach(player -> {
				player.send(Cmd.C2S_LEAVE_GAME, data);;
			});
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void onPlayerRelive(GameEvent e){
		Player player = (Player) e.getData();
		GameSession session = player.getSession();
		if(session == null || !session.inState(GameState.IN_GAME))
			return;
		
		JSONObject data = new JSONObject();
		data.put("g", player.getGroup());
		data.put("i", player.getIndex());
		data.put("x", player.x);
		data.put("y", player.y);
		
		m_worlds.get(session.getStateData().int1).foreach(p->{
			p.send(Cmd.S2C_RELIVE, data);
		});
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
