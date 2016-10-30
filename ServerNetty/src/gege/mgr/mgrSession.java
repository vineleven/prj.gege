package gege.mgr;

import gege.common.GameSession;
import gege.net.channel.GameChannelGroup;
import gege.util.Logger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;




/**
 * 
 * 
 * @author vineleven
 *
 */

public class mgrSession {
	
	static private ChannelGroup m_channelGroup;
	
	private static HashMap<ChannelId, GameSession> m_connectsMap = new HashMap<>();
	
	
	
	
	
	public static void initialize(){
		m_channelGroup = new GameChannelGroup(
				GlobalEventExecutor.INSTANCE,
				mgrSession::removeGameSession
			);
	}

	
	public static void dispatchRequest(Channel chn, String msg){
		GameSession session = getGameSession(chn.id());
		if(session != null){
			// 验证连接是否合法
			session.receive(msg);
		} else
			Logger.error("can't find game sessoin by channel:" + chn.id().toString());
	}
	
	
	public static void putChannel( Channel c ){
		m_channelGroup.add( c );
		putSession(c.id(), new GameSession(c));
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
	
	
	
	private static void putSession(ChannelId channelId, GameSession session){
		synchronized( m_connectsMap ){
			m_connectsMap.put( channelId, session );
		}
	}
	
	
	public static GameSession getGameSession(ChannelId channelId){
		return m_connectsMap.get(channelId);
	}
	
	
	private static void removeGameSession(ChannelId id){
		synchronized( m_connectsMap ){
			GameSession session = m_connectsMap.remove(id);
			if( session != null )
				session.onRemove();
		}
	}
	
	
	
}
