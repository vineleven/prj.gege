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
				game.start();
				Tools.debug( "Wait For Connect: " + server.toString() + "\n" );
				while( true ) {
					Socket client = server.accept();
					GameSession session = new GameSession(client);
					game.login(session);
					executor.execute(session);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Tools.debug("Game Server Closed.");
				game.close();
				executor.shutdown();
				server.close();
			}
		} catch (IOException e) {
			Tools.error( "startServer err" );
		}
	}
	
	
	static class GameSession implements Runnable {
		private Socket m_client;
		private BufferedInputStream m_input;
		private BufferedOutputStream m_output;
		private final String m_remoteAddr;
		private String m_curDate;
		private Callback m_closeCallback = null;
		
		
		public GameSession(Socket client) throws IOException {
			m_client = client;
			m_input = new BufferedInputStream( client.getInputStream() );
			m_output = new BufferedOutputStream( client.getOutputStream() );
			m_remoteAddr = client.getRemoteSocketAddress().toString();
			
			m_curDate = Tools.getCurDate();
			Tools.debugf( "%s connect: %s", m_curDate, m_remoteAddr);
		}
		
		
		public String getRemoteAddr(){
			return m_remoteAddr;
		}
		
		
		public void setCloseCallback(Callback closeCallback){
			m_closeCallback = closeCallback;
		}
		
		
		public void close(){
			if(m_closeCallback != null){
				m_closeCallback.invoke();
				m_closeCallback = null;
			}
			
			try {
				m_input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				m_output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				m_client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
//			clientList.remove( PID );
			
			Tools.debugf( "%s closed.", m_remoteAddr );
		}
		
		
		@Override
		public void run() {
			try {
				while ( true ) {
					JSONObject req = receive(m_input);
					if(req == null)
						break;
//					Tools.debugf( "request:%s", obj.toString() );
					
					game.request(req);
				}
			} catch ( Exception e ) {
				Tools.errorf( "%s clien: %s" , m_remoteAddr, e.getMessage() );
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
					if( result == -1 ){
						Tools.error("msg is not broken:" + rqStr.toString());
						return null;
					}
					
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
