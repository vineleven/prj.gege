package gege.net.server;

import gege.exception.ServerException;
import gege.mgr.mgrSession;
import gege.util.Logger;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;



public class GameMainHandler extends ChannelHandlerAdapter {

	
	public GameMainHandler() {
	}
	
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		mgrSession.putChannel( ctx.channel() );
//		System.out.println( "channelActive" + this.toString() );
	}
	
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
	
	
	@Override
	public void channelRead( ChannelHandlerContext ctx, Object msg) throws Exception {
		String requestMsg = ( String ) msg;
//		Logger.debug( Tools.getCurDate() + " request:" + requestMsg );
		
		try {
			mgrSession.dispatchRequest(ctx.channel(), requestMsg);
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
