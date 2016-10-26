package gege.net.server;

import gege.consts.Global;
import gege.util.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class LoginServer extends BaseServer {
	
	public static void main(String[] args) {
		new LoginServer().boot();
	}
	
	
	
	private static LoginServer inst = null;
	
	private ServerBootstrap server;
	EventLoopGroup groupAcceptor;
	EventLoopGroup groupWorker;

	
	private LoginServer() {
	}
	
	
	
	public synchronized static LoginServer create(){
		if( inst == null ){
			inst = new LoginServer();
		}
		
		return inst;
	}
	
	
	public static LoginServer getInstance(){
		
		return inst;
	}
	
	
	@Override
	public void boot(){
		groupAcceptor = new NioEventLoopGroup( Global.LOGIN_SERVER_ACCEPTOR_EVENT_LOOP_COUNT );
		groupWorker = new NioEventLoopGroup( Global.LOGIN_SERVER_WORKER_EVENT_LOOP_COUNT );
		
		server = new ServerBootstrap();
		server.group( groupAcceptor, groupWorker )
			.channel( NioServerSocketChannel.class )
			.option( ChannelOption.SO_BACKLOG, 100 )
			.handler( new LoggingHandler( LogLevel.ERROR ) )
			.childHandler( new LoginServerChannelHandler() );
		

		try {
			ChannelFuture f = server.bind( Global.LOGIN_SERVER_PORT ).sync();
			
			Logger.info( "===============================================" );
			Logger.info( "  ==========> login server start. <==========" );
			Logger.info( "===============================================" );
			
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			groupAcceptor.shutdownGracefully();
			groupWorker.shutdownGracefully();
			
			Logger.info( "==================================================" );
			Logger.info( "  ==========> login server shutdown. <==========" );
			Logger.info( "==================================================" );
		}
	}
	
	
	@Override
	public void close(){
		groupAcceptor.shutdownGracefully();
		groupWorker.shutdownGracefully();
	}

	
	class LoginServerChannelHandler extends ChannelInitializer<SocketChannel>{
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast( new DelimiterBasedFrameDecoder( 1024, Global.MSG_DELIMITER ) );
			ch.pipeline().addLast( new StringDecoder() );
			ch.pipeline().addLast( "readTimeoutHandler", new ReadTimeoutHandler(50));
//		IdleStateHandler
			ch.pipeline().addLast( new LoginServerHandler() );
		}
		
		
	}
}




