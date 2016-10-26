package gege.game;

import gege.common.SyncQueue;
import gege.common.TickThread;
import gege.consts.Global;

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
	
	
	
	
	public static SyncQueue<JSONObject> requestQueue = new SyncQueue<>( Global.GAME_REQUEST_QUEUE_MAX_SIZE );
	
	public static void dispatchRequest(JSONObject req){
		// 验证连接是否合法
		requestQueue.enqueue( req );
	}
	
	
	
	
	
	
	
	
	
	private ArrayList<Player> mPlayerList = new ArrayList<>( 256 );

	
	private Game(){ super( Global.GAME_UPDATE_INTERVAL ); }
	
	
	private void init(){
		// 游戏基础数据
		
		int interval = getInterval();
		// 更新器
//		mReqCyc = new Cycler(Global.GAME_PROCESS_REQUEST_INTERVAL / interval, this::procRequest );
	}
	
	
	public void procRequest(){
//		Request req;
//		int count = 0;
//		while( count < Global.GAME_MAX_PROCESS_REQUEST && ( req = requestQueue.dequeueImm() ) != null ){
//			req.call();
//			count++;
//		}
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	

}
