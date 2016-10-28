package gege.game;

import gege.common.EventDispatcher;
import gege.common.Request;
import gege.common.SyncQueue;
import gege.common.TickThread;
import gege.consts.Cmd;
import gege.consts.Event;
import gege.consts.Global;
import gege.util.Tools;

import java.util.ArrayList;

import org.json.JSONObject;


/**
 * 
 * Game内只有Room的集合
 * @author vineleven
 *
 */
public class Game extends TickThread {

	private static Game inst = null;
	
	
	public synchronized static Game create(){
		if( inst == null ){
			inst = new Game();
			inst.init();
			
		}

		return inst;
	}
	
	
	public static Game getInstance() { return inst; }
	
	
	
	
	
	
	
	
	
	
	
	
	
	public SyncQueue<Request> m_requestQueue = new SyncQueue<>( Global.GAME_REQUEST_QUEUE_MAX_SIZE );
	
	private ArrayList<Player> mPlayerList = new ArrayList<>( 256 );

	
	private Game(){ super( Global.GAME_UPDATE_INTERVAL ); }
	
	
	private void init(){
		// 游戏基础数据
		
		EventDispatcher.getGlobalInstance().addListener(Event.NET_REQUEST, this::onRequest);
		
		int interval = getInterval();
		// 更新器
//		mReqCyc = new Cycler(Global.GAME_PROCESS_REQUEST_INTERVAL / interval, this::procRequest );
	}
	
	
	public boolean onRequest(Object data){
		Request req = (Request) data;
		m_requestQueue.enqueue(req);
		return true;
	}
	
	
	public void procRequest(){
		// 理论需要处理所有游戏相关的请求
		Request req;
		int count = 0;
		try{
			while( count < Global.GAME_MAX_PROCESS_REQUEST && ( req = m_requestQueue.dequeueImm() ) != null ){
//			req.call();
				procRequest(req);
				count++;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	private void procRequest(Request req) throws Exception {
		switch (req.cmd) {
		case Cmd.C2S_GET_TIME:
			if(Tools.getRandomInt(0, 2) == 0){
				JSONObject data = new JSONObject();
				data.put("rnd", Tools.getRandomInt(100, 1000));
				req.getSession().send(1, data);
			}
			break;

		default:
			break;
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
	
	
	
	
	

}
