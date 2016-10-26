package gege.net.server;

import gege.consts.Global;
import gege.util.Logger;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;



public class HeartBeatHandler extends ChannelHandlerAdapter {
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String request = (String) msg;
		if( Global.REQUEST_HEART_BEAT.equals( request ) ){
			Logger.debug( "client HeartBeat." );
			ctx.writeAndFlush( Unpooled.copiedBuffer( Global.REQUEST_HEART_BEAT.concat( Global.MSG_END_FLAG ).getBytes() ) );
		} else{
			ctx.fireChannelRead(msg);
		}
	}
}
