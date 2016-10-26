package gege.test;

import gege.net.server.BaseServer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySQLServer_old extends BaseServer {
	
	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch ( ClassNotFoundException e ) {
			throw new ExceptionInInitializerError( "Load mysql driver err" );
		}
	}
	
	private Connection connect;
	
	
	public void connect() throws SQLException {
//		connect = DriverManager.getConnection(
//			String.format( "jdbc:mysql://%s:3306/mysql", Const.MYSQL_SERVER_ADRESS, Const.MYSQL_SERVER_PORT, Const.MYSQL_DB_NAME ),
//			Const.MYSQL_USER_NAME,
//			Const.MYSQL_PASSWORD );
//		connect.setAutoCommit( false );
//		
//			Statement stmt = connect.createStatement();
//
//			Logger.info( "Connect Mysql server SUCCESS!" );
//			
//			ResultSet rs = stmt.executeQuery("select * from user");
//			while (rs.next()) {
//				System.out.println(rs.getString("host"));
//			}
//			
//			rs.close();
//			stmt.close();
//		connect.close();
	}
	
	
	public void get(){
		
	}
	
	
	public void execute( String sql ){
		Statement stmt = null;
//		connect.prepareStatement(sql);
		try {
			stmt = connect.createStatement();
			stmt.execute( sql );
		} catch (SQLException e) {
			e.printStackTrace();
			
			try {
				connect.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				connect.commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			if( stmt != null )
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	
	public void execute( ArrayList<String> sqls ){
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			for (int i = 0; i < sqls.size(); i++) {
				stmt.execute( sqls.get( i ) );
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connect.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				connect.commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			if( stmt != null )
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}


	@Override
	public void boot() {
		
	}


	@Override
	public void close() {
		
	}


	
}
