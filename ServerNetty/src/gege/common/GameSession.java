package gege.common;

import gege.consts.EventId;
import gege.consts.GameState;
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
	
	
	public interface OnRemove{
		void onRemove(GameSession session);
	}
	
	private Channel m_chn;
	
	private String m_playerName = "";
	
	private GameState m_state = GameState.IDLE;
	
	private OnRemove m_onRemove = null;
	
	// 不同状态下有不同含义(room 表示roomIdx  world内表示playerIdx)
	private StateData m_stateData;


	public GameSession(Channel chn) {
		m_chn = chn;
		if(chn != null)
			m_playerName = m_chn.remoteAddress().toString();
	}
	
	
	
	public String getPlayerName(){
		return m_playerName;
	}
	
	
	public void setName(String name){
		m_playerName = name;
	}
	
	
	public void setState(GameState state, StateData data){
		m_state = state;
		m_stateData = data;
	}
	
	
	public StateData getStateData(){
		return m_stateData;
	}
	
	
	public boolean inState(GameState state){
		return m_state.equals(state);
	}
	
	
	public void setOnDisconnect(OnRemove onRemove){
		m_onRemove = onRemove;
	}
	
	
	/**
	 * 接受消息
	 */
	public void receive(String msg){
		if(m_chn == null){
			Logger.error( "Session receive: channel is null." );
			return;
		}
		
		try{
			Request req = new Request(this, msg);
			EventDispatcher.getGlobalInstance().dispatchEvent(EventId.GLOBAL_REQUEST, req);
		} catch(JSONException e){
			Logger.error( "receive an invalid msg [" + msg + "]" );
		}
	}
	
	
	/**
	 * 发送消息
	 */
	public void send(int cmd, JSONObject data) {
		if( m_chn == null ){
			Logger.error( "Session send: channel is null." );
			return;
		}
		
		if(data == null){
			data = new JSONObject();
		}
		
		JSONObject sendData = new JSONObject();

		sendData.put( Global.MSG_KEY_CMD, cmd );
		sendData.put( Global.MSG_KEY_DATA, data );

		String msg = sendData.toString().concat( Global.MSG_END_FLAG );
		
//		Logger.debug( "send:" + msg );
		
		m_chn.writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));
	}
	
	
	public void onRemove(){
		Logger.debug("disconnect:" + m_chn.toString());
		m_chn = null;
		if(m_onRemove != null){
			m_onRemove.onRemove(this);
			m_onRemove = null;
		}
	}
	
	
	public boolean enabled(){
		return m_chn != null;
	}
}
