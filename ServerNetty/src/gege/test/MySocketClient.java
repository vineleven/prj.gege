package gege.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MySocketClient extends Thread {
	public static void main(String[] args) {
		new MySocketClient().start();
	}
	
	
	private BufferedOutputStream output;
	private BufferedInputStream input;
	
//	private BufferedReader reader;
	Socket socket;
	@Override
	public void run() {
		try {
			String ip = "122.232.228.255";
			int port = 3128;
			socket = new Socket( ip, port );
			output = new BufferedOutputStream( socket.getOutputStream() );
			input = new BufferedInputStream( socket.getInputStream() );
//			reader = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			
//			new HeartBeatThread( output ).start();
			while ( true ) {
				output.write( "GET http://www.baidu.com/ HTTP/1.1\r\n\r\n".getBytes() );
				output.flush();
				
				int i;  
				StringBuffer str = new StringBuffer();
				while ((i = input.read()) > 0) {  
					str.append( (char) i );
				} 
				
				System.out.println( "input----\n" + str.toString() );
				
//				String str = reader.readLine();
				
//				System.out.println( "str----" + str );
				break;
				
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
		} finally {
			try {
				System.out.println( "client close" );
				output.close();
				input.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}


class HeartBeatThread extends Thread {
	BufferedOutputStream output;
	
	public HeartBeatThread( BufferedOutputStream o ) {
		output = o;
	}
	
	
	@Override
	public void run() {
		byte[] data = "HB$".getBytes();
		while ( true ) {
			try {
				output.write( data );
				output.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			try {
				Thread.sleep( 1000 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
