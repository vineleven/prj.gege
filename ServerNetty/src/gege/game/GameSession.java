package gege.game;

import gege.consts.Global;
import gege.util.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import org.json.JSONObject;



public class GameSession {
	private Channel m_chn;
//	private String m_uid;
	


	
	private static ByteBuf getSendMsg( int cmd, JSONObject data ){
		JSONObject j = new JSONObject();

		j.put( Global.MSG_KEY_CMD, cmd );
		j.put( Global.MSG_KEY_DATA, data );
		
		String msg = j.toString().concat( Global.MSG_END_FLAG );
//		Logger.debug( "send:" + msg );
		return Unpooled.copiedBuffer( msg.getBytes() );
	}
	
	
	/**
	 * 发送消息
	 */
	public void send(int cmd, JSONObject data) {
		if( m_chn == null ){
			Logger.error( "Response SendTo Error, channel is null." );
			return;
		}
		
//		if( cmd == DEFAULT_CMD )
//			throw new RequestException( "Response Need Declare a Cmd." );
		
		m_chn.writeAndFlush( getSendMsg( cmd, data ) );
//		Logger.debug( cmd + " Has Sended, Channel Active: " + m_chn.isActive() );
	}
	
	
	
	
	
	public void onRemove(){
		m_chn = null;
	}
}
