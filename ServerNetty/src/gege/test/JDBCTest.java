package gege.test;

import gege.util.DBUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class JDBCTest {
	public static void main(String args[]) {
		try {
			Class.forName("com.mysql.jdbc.Driver"); // 加载MYSQL JDBC驱动程序
			// Class.forName("org.gjt.mm.mysql.Driver");
			System.out.println("Success loading Mysql Driver!");
		} catch (Exception e) {
			System.out.print("Error loading Mysql Driver!");
			e.printStackTrace();
		}
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/testdb", "root", "pw123");
			
			connect.setAutoCommit( false );

			
			String sql;
			sql = "update table1 set num=? where id=?";
			sql = "insert into table1(id, name, num) values( ?,?,? )";
			sql = "select * from conf_db";
			PreparedStatement pstm = connect.prepareStatement( sql );
			
			long startTime = System.currentTimeMillis();
			
			ResultSet rs = pstm.executeQuery();

			long endTime = System.currentTimeMillis();
			
			int tc = 0;
			rs.next();
			for (int i = 0; i < 3; i++) {
				System.out.println( "---> " + rs.getString( "fieldName" ) );
			}
			
			System.out.println( "useTime:" + ( endTime - startTime ) + " count:" + tc );
			
			
			
//			ResultSet rs = pstm.executeQuery();
			
//			while( rs.next() ){
//				String passWord = rs.getString( "passWord" );
//				String charName = rs.getString( "charName" );
//				int uid = rs.getInt( "uid" );
//				String email = rs.getString( "email" );
//				
//				System.out.println( "passWord:" + passWord + "  charName:" + charName + " uid:" + uid + " email:" + email );
//			}
			
			
			
			
			pstm.close();
			connect.close();
		} catch ( SQLException e ) {
			System.out.println("get data error!" + e.getMessage() );
			e.printStackTrace();
		} finally {
			System.out.println( "complete" );
		}
	}
	
	
	public static void main2(String args[]) {
		try {
			Connection con = DBUtil.getConnection();
			PreparedStatement pstm = con.prepareStatement( "update table1 set num=? where id=?" );
			pstm.setInt(1, 345);
			pstm.setInt(2, 11);
			pstm.executeUpdate();
			
			System.out.println( con );
			
			pstm.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
