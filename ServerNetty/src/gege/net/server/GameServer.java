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

public class GameServer extends BaseServer {
	
	public static void main(String[] args) {
		new GameServer().boot();
	}
	
	
	private static GameServer inst = null;
	
	private ServerBootstrap server;
	EventLoopGroup groupAcceptor;
	EventLoopGroup groupWorker;

	
	private GameServer() {
	}
	
	
	public synchronized static GameServer create(){
		if( inst == null ){
			inst = new GameServer();
		}
		
		return inst;
	}
	
	
	public static GameServer getInstance(){
		return inst;
	}
	
	
	@Override
	public void boot(){
		groupAcceptor = new NioEventLoopGroup( Global.GAME_SERVER_ACCEPTOR_EVENT_LOOP_COUNT );
		groupWorker = new NioEventLoopGroup( Global.GAME_SERVER_WORKER_EVENT_LOOP_COUNT );
		
		server = new ServerBootstrap();
		server.group( groupAcceptor, groupWorker )
			.channel( NioServerSocketChannel.class )
			.option( ChannelOption.SO_BACKLOG, 100 )
			.handler( new LoggingHandler( LogLevel.ERROR ) )
			.childHandler( new GameServerChannelHandler() );
		

		try {
//			mgrControl.start();

			ChannelFuture f = server.bind( Global.GAME_SERVER_PORT ).sync();
			
			Logger.info( "==============================================" );
			Logger.info( "  ==========> game server start. <==========" );
			Logger.info( "==============================================" );
			
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			groupAcceptor.shutdownGracefully();
			groupWorker.shutdownGracefully();
			
			Logger.info( "=================================================" );
			Logger.info( "  ==========> game server shutdown. <==========" );
			Logger.info( "=================================================" );
		}
	}
	
	
	@Override
	public void close(){
		groupAcceptor.shutdownGracefully();
		groupWorker.shutdownGracefully();
	}

	
	class GameServerChannelHandler extends ChannelInitializer<SocketChannel>{
		/**
		 * 目前只做转发服务器，用不着这些功能
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast( new DelimiterBasedFrameDecoder( 1024, Global.MSG_DELIMITER ) );
			ch.pipeline().addLast( new StringDecoder() );
			ch.pipeline().addLast( "readTimeoutHandler", new ReadTimeoutHandler(50));
//		IdleStateHandler
			ch.pipeline().addLast( new HeartBeatHandler() );
			ch.pipeline().addLast( new GameMainHandler() );
		}
		*/
		
		
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast( new DelimiterBasedFrameDecoder( 1024, Global.MSG_DELIMITER ) );
			ch.pipeline().addLast( new StringDecoder() );
			ch.pipeline().addLast( "readTimeoutHandler", new ReadTimeoutHandler(50));
//		IdleStateHandler
			ch.pipeline().addLast( new HeartBeatHandler() );
			ch.pipeline().addLast( new GameMainHandler() );
		}
	}
	
	
	
}




