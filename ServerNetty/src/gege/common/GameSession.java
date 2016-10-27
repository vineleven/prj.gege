package gege.common;

import gege.consts.Event;
import gege.consts.Global;
import gege.util.Logger;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 连接request和channel
 * <p>
 * 接受请求和发起回复
 * 
 * @author vineleven
 *
 */
public class GameSession {
	private Channel m_chn;
	


	public GameSession(Channel chn) {
		m_chn = chn;
	}
	
	
	/**
	 * 接受消息
	 */
	public void receive(String msg){
		try{
			Request req = new Request(this, msg);
			EventDispatcher.getGlobalInstance().dispatchEvent(Event.NET_REQUEST, req);
		} catch(JSONException e){
			Logger.error( "receive an invalid msg [" + msg + "]" );
		}
	}
	
	
	/**
	 * 发送消息
	 */
	public void send(int cmd, JSONObject data) {
		if( m_chn == null ){
			Logger.error( "Response SendTo Error, channel is null." );
			return;
		}
		
		JSONObject sendData = new JSONObject();

		sendData.put( Global.MSG_KEY_CMD, cmd );
		sendData.put( Global.MSG_KEY_DATA, data );
		
		String msg = sendData.toString().concat( Global.MSG_END_FLAG );
		
		m_chn.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));
	}
	
	
	public void onRemove(){
		m_chn = null;
	}
}
