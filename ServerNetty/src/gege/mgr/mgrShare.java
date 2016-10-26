package gege.mgr;

import gege.game.GameSession;
import gege.net.channel.GameChannelGroup;
import gege.util.Tools;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;




/**
 * 
 * 共享的一些数据都放这里，需要注意多线程并发问题
 * 
 * @author vineleven
 *
 */

public class mgrShare {
	
	// 每次起动会产生一个随机数，用于某些验证或者加密
	final public static int RANDOM_INT = Tools.getRandomInt( 123456, 654321 );
	
	
	private static mgrShare inst = null;
	
	public static void initialize(){
		if( inst == null )
			inst = new mgrShare();
	}
	
	
	public static mgrShare getInstance(){
		return inst;
	}
	
	

	
	
	/**************************************一下类成员***************************************/
	
	final private ChannelGroup gameChannelGroup;
	private HashMap<ChannelId, GameSession> connectsMap = new HashMap<>();
	
	
	
	public mgrShare() {
		gameChannelGroup = new GameChannelGroup(
			GlobalEventExecutor.INSTANCE,
			id-> removeGameSession( id )
		);
	}
	
	
	
	
	
	public void putChannel( Channel c ){
		gameChannelGroup.add( c );
	}
	
	
//	public Channel getChannel( ChannelId id ){
//		return gameChannelGroup.find( id );
//	}
	
	
//	public int getChannelCount(){
//		return gameChannelGroup.size();
//	}
	
	
//	public void closeChannel(){
//		gameChannelGroup.close().awaitUninterruptibly();
//	}
	
	
	
	public void putSession(ChannelId channelId, GameSession session){
		synchronized( connectsMap ){
			connectsMap.put( channelId, session );
		}
	}
	
	
	private void removeGameSession(ChannelId id){
		synchronized( connectsMap ){
			GameSession session = connectsMap.remove(id);
			if( session != null )
				session.onRemove();
		}
	}
	
	
	
}
