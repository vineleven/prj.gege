package gege.game;

import gege.data.Config;
import gege.util.Tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

public class GameServer extends Thread {
	
	/****************************** 启动代码 **********************************/
	public static void main(String[] args) {
		new GameServer().start();
	}
	/************************************************************************/
	
	
	
	
	

	private static final ExecutorService executor = Executors.newCachedThreadPool();
	private static Game game = null;
	
	
	
	public GameServer() {
		game = new Game();
		
		try {
			Tools.debug("Cur Host:" + InetAddress.getLocalHost().toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket( Config.SERVER_PORT );
			try {
				Tools.debug( "Wait For Connect: " + server.toString() + "\n" );
				while( true ) {
					Socket client = server.accept();
					executor.execute( new ServerThread( client ) );
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Tools.debug("Game Server Closed.");
				executor.shutdown();
				server.close();
			}
		} catch (IOException e) {
			Tools.error( "startServer err" );
		}
	}
	
	
	static class ServerThread implements Runnable {
		private Socket client;
		private BufferedInputStream input;
		private BufferedOutputStream output;
		private String remoteSocketAddress;
		private String curDate;
		
		public ServerThread( Socket client ) throws IOException {
			this.client = client;
			input = new BufferedInputStream( client.getInputStream() );
			output = new BufferedOutputStream( client.getOutputStream() );
			remoteSocketAddress = client.getRemoteSocketAddress().toString();
			
			curDate = Tools.getCurDate();
			Tools.debugf( "%s connect: %s", curDate, remoteSocketAddress);
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
			
//			clientList.remove( PID );
			
			Tools.debugf( "%s closed.", remoteSocketAddress );
		}
		
		
		@Override
		public void run() {
			try {
				while ( true ) {
					
					Tools.debugf( "request:%d\n%s", result, rqStr.toString() );
					
				}
			} catch ( IOException e ) {
				Tools.errorf( "%s clien: %s" , remoteSocketAddress, e.getMessage() );
			} finally{
				close();
			}
		}
		
		
		// 解析数据为json
		public static JSONObject receive(BufferedInputStream input){
			try {
				int result;
				byte[] lenArr = new byte[4];
				// 两个字节表示长度
				for (int i = 0; i < 2; i++) {
					result = input.read();
					if( result == -1 ){
						Tools.error("can't get msg len.");
						return null;
					}
					
					lenArr[i] = (byte) result;
				}
				
				int len = Tools.bytesToInt(lenArr);
				StringBuffer rqStr = new StringBuffer();
				
				for (int i = 0; i < len; i++) {
					result = input.read();
					if( result == -1 )
						return null;
					
					rqStr.append((char)result);
				}
				
				JSONObject obj = new JSONObject(rqStr.toString());
				return obj;
			} catch (IOException | JSONException e) {
				e.printStackTrace();
				Tools.error("rsp decode error.");
			}
			return null;
		}
	}
}
