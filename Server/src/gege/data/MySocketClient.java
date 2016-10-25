package gege.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.InputMap;

public class MySocketClient extends Thread {
	public static void main(String[] args) {
		new MySocketClient().start();
	}
	
	
	private BufferedOutputStream output;
	private BufferedInputStream input;
	
	private BufferedReader reader;
	Socket socket;
	@Override
	public void run() {
		try {
			socket = new Socket( "192.168.1.45", Config.SERVER_PORT );
			output = new BufferedOutputStream( socket.getOutputStream() );
//			input = new BufferedInputStream( socket.getInputStream() );
			reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			while ( true ) {
				byte[] b = new byte[200];
//				System.in.read( b );
//				System.out.println( "input----" + b.toString() );
				output.write( "asdf /GET_PID asd".getBytes() );
				output.flush();
				
				String str = reader.readLine();
				
				System.out.println( "str----" + str );
				
//				if( str == null )
//					break;
			}
//				socket.close();
				
		} catch (UnknownHostException e) {
//			e.printStackTrace();
			System.err.printf( "%s\n", e.getMessage() );
		} catch (IOException e) {
//			e.printStackTrace();
			System.err.printf( "%s\n", e.getMessage() );
		}
	}
}
