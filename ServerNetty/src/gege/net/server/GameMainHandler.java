package gege.net.server;

import gege.consts.CODE;
import gege.consts.Global;
import gege.exception.ServerException;
import gege.game.Game;
import gege.mgr.mgrProto;
import gege.mgr.mgrShare;
import gege.net.proto.GameRequest;
import gege.net.proto.Request;
import gege.net.proto.Response;
import gege.util.Logger;
import gege.util.Tools;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.json.JSONException;
import org.json.JSONObject;



public class GameMainHandler extends ChannelHandlerAdapter {

	
	public GameMainHandler() {
	}
	
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		mgrShare.getInstance().putChannel( ctx.channel() );
//		System.out.println( "channelActive" + this.toString() );
	}
	
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
	
	
	@Override
	public void channelRead( ChannelHandlerContext ctx, Object msg) throws Exception {
		String requestMsg = ( String ) msg;
		Logger.debug( Tools.getCurDate() + " request:" + requestMsg );
		
		int cmd = -1;
		try {
			JSONObject requestObj = new JSONObject( requestMsg );
			cmd = requestObj.getInt( Global.MSG_KEY_CMD );
			if( cmd < 0 ) return;
			
			Request request = mgrProto.getRequestProto( cmd );
			
			if( request == null ){
				Logger.debug( "can't find request cmd: [" + cmd + "]" );
				return;
			}

			JSONObject data = requestObj.getJSONObject( Global.MSG_KEY_DATA );
			
			request.decode( data );
			request.setChannelId( ctx.channel().id() );
			
			if( request instanceof GameRequest )
				Game.dispatchRequest( request );
			else
				request.call();

		} catch ( JSONException e) {
			Logger.error( "decode json error in game server. msg [" + requestMsg + "]" );
			new Response(cmd, CODE.MSG.INVALID).sendTo(ctx.channel().id());
		} catch ( ServerException e ){
			e.printStackTrace();
		} catch (Exception e) {
			Logger.error( "This Is Not a GameException." );
			e.printStackTrace();
		} finally {
			ctx.fireChannelRead( msg );
		}
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		cause.printStackTrace();
		Logger.error( cause.getMessage() + cause.getCause() );
		// 最好不要主动关闭，直接抛出异常
//		ctx.close();
	}
	
	
	
}
