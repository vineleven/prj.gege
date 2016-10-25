package gege.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTemplate extends Thread {
	
	public static void main(String[] args) {
		new ServerTemplate().start();
	}
	
	private static DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
	private static LinkedList< Socket > clientList = new LinkedList<Socket>();
	private static ExecutorService executor = null;


	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket( Config.SERVER_PORT );
			executor = Executors.newCachedThreadPool();
			try {
				System.out.println( "Wait For Connect: " + server.toString() + "\n" );
				while( true ) {
					Socket client = server.accept();
					executor.execute( new ServerThread( client ) );
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				clientList.clear();
				executor.shutdown();
				server.close();
			}
		} catch (IOException e) {
			System.err.println( "startServer err" );
		}
	}
	
	
	static class ServerThread implements Runnable {
		private Socket client;
		private BufferedInputStream input;
		private BufferedOutputStream output;
		private long lastTime = 0;
		private String remoteSocketAddress;
		
		
		public ServerThread( Socket client ) throws IOException {
			this.client = client;
			input = new BufferedInputStream( client.getInputStream() );
			output = new BufferedOutputStream( client.getOutputStream() );
			lastTime = System.currentTimeMillis();
			remoteSocketAddress = client.getRemoteSocketAddress().toString();
			
			Date date = new Date( lastTime );
			String curDate = dateFormat.format( date );
			System.out.printf( "%s connect in %s\n", remoteSocketAddress, curDate );
		}
		
		
		public void close(){
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			clientList.remove( client );
			
			System.out.printf( "%s closed.\n", remoteSocketAddress );
		}
		
		
		@Override
		public void run() {
			while ( true ) {
				try {
					byte[] request = new byte[ 1000 ];
					int result = input.read( request );
					String rqStr = new String( request );
					System.out.printf( "request:%d\n%s\n", result, rqStr );
					
					if( result == -1 ){
						close();
						break;
					}

					// 是否延迟处理
//					if( Config.SERVER_RESPONSE_INTERVAL_OPEN ){
//						long curTime = System.currentTimeMillis();
//						long dt = Config.SERVER_RESPONSE_INTERVAL - ( curTime - lastTime );
//						lastTime = curTime;
//						if( dt > 0 ){
//							try {
////								System.out.println( "sleep" + dt );
//								Thread.sleep( dt );
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
//					}
				} catch ( IOException e ) {
//					e.printStackTrace();
					System.err.printf( "%s IOException by %s\n" , remoteSocketAddress, e.getMessage() );
					close();
					break;
				}
			}
		}
	}
}
