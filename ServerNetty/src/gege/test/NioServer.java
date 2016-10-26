package gege.test;

import gege.consts.Global;
import gege.net.server.BaseServer;
import gege.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer extends BaseServer implements Runnable {
	
	/****************************** 启动代码 **********************************/
	public static void main(String[] args) {
		new NioServer();
	}
	/************************************************************************/
	
	
	private Selector selector = null;
	private ServerSocketChannel serverSocketChannel = null;
	
	public NioServer() {
		try {
			serverSocketChannel = ServerSocketChannel.open();
		
			ServerSocket serverSocket = serverSocketChannel.socket();
//			serverSocket.setReuseAddress( true );
			serverSocket.bind( new InetSocketAddress( Global.GAME_SERVER_PORT ) );
			
			serverSocketChannel.configureBlocking( false );
			
			selector = Selector.open();
			serverSocketChannel.register( selector, SelectionKey.OP_ACCEPT );
			
		} catch (IOException e) {
			Logger.error( "open ServerSocketChannel error!" );
		}
	}
	

	@Override
	public void run() {
		try {
			while ( true ) {
				if( selector.select() <= 0 ) return;
				Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
				while ( keyIterator.hasNext() ) {
					SelectionKey key = ( SelectionKey ) keyIterator.next();
					keyIterator.remove();
					if( key.isAcceptable() ){
						newConnect( key );
					} else if( key.isReadable() ){
						receive( key );
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	public void newConnect( SelectionKey key ) throws IOException{
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
		SocketChannel socket = serverSocketChannel.accept();
		
		socket.configureBlocking( false );
		socket.register( selector, SelectionKey.OP_READ );
	}
	
	
	public void receive( SelectionKey key ){
		
	}


	@Override
	public void boot() {
		
	}


	@Override
	public void close() {
		
	}

}
