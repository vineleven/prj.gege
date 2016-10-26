package gege.net.server;

import gege.exception.ServerException;
import gege.util.Logger;
import gege.util.Tools;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.json.JSONException;

public class LoginServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		/*
		mgrShare.getInstance().putChannel( ctx.channel() );
		 */
	}
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String request = ( String ) msg;
		Logger.debug( Tools.getCurDate() + " request:" + request );
		try {
			/*
			JSONObject requestObj = new JSONObject( request );
			
			int cmd = requestObj.getInt( Global.MSG_KEY_CMD );
			if( cmd < 0 ) return;
			
			Request proto = mgrProto.getRequestProto( cmd );
			
			if( proto == null ){
				Logger.debug( "can't find request cmd: [" + cmd + "]" );
				return;
			}
			JSONObject data = requestObj.getJSONObject( Global.MSG_KEY_DATA );
			
			proto.decode( data );
			proto.setChannelId( ctx.channel().id() );
			
			// 这里直接处理消息并回复
			proto.call();
			*/
		} catch ( JSONException e) {
			Logger.error( "decode json error in login server. msg:[" + request + "]" );
		} catch ( ServerException e ){
			e.printStackTrace();
		} catch (Exception e2) {
			Logger.error( "This Is Not ServerException." );
			e2.printStackTrace();
		} finally {
			ctx.fireChannelRead( msg );
		}
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Logger.error( cause.getMessage() + cause.getCause() );
	}
}
