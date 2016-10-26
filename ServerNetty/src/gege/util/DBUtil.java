package gege.util;

import gege.db.common.BatchExecutor;
import gege.db.common.Queryable;
import gege.net.server.MySQLServer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;



/**
 * 数据库工具
 * @author vineleven
 *
 */
public class DBUtil {
	private static DataSource dataSrc = null;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Properties prop = new Properties();
			prop.load(MySQLServer.class.getClassLoader().getResourceAsStream("dbcpconfig.properties"));
			dataSrc = BasicDataSourceFactory.createDataSource(prop);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError("MySQLServer initialized error.");
		}
	}
	
	
	public static void initialize(){
		Logger.info( "mySql initialize complete." );
	}
	

	public static DataSource getDataSource() {
		return dataSrc;
	}
	

	public static Connection getConnection() throws SQLException {
		return dataSrc.getConnection();
	}
	
	
	public static boolean query( String sql, Queryable query, Object ... values ){
		if( sql == null || values == null ){
			return false;
		}
		
		boolean done = false;
		try {
			Connection con = getConnection();
			PreparedStatement st = con.prepareStatement( sql );
			
			for (int i = 0; i < values.length; i++) {
				st.setObject( i + 1, values[i] );
			}
			
			try {
				ResultSet rs = st.executeQuery();
				if( rs.next() ){
					query.makeResult( rs );
				}
				rs.close();
				done = true;
			} catch ( SQLException e ){
				e.printStackTrace();
			}
			
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return done;
	}
	
	
	public static int update( String sql, Object ... values ){
		if( sql == null || values == null || values.length < 1 ){
			return 0;
		}
		
		int result = 0;
		try {
			Connection con = getConnection();
			
			PreparedStatement st = con.prepareStatement( sql );
			
			for (int i = 0; i < values.length; i++) {
				st.setObject( i + 1, values[i] );
			}
			
			try {
				result = st.executeUpdate();
				con.commit();
			} catch ( SQLException e ){
				e.printStackTrace();
				result = 0;
			}
			
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**
	 * 批量处理
	 * @param sql
	 * @param valuesArr
	 * @return
	 */
	public static int updateBatch( String sql, Object[] ... valuesArr ){
		if( sql == null || valuesArr == null || valuesArr.length < 1 || valuesArr[0].length < 1 ){
			return 0;
		}
		
		int result = 0;
		try {
			Connection con = getConnection();
			
			PreparedStatement st = con.prepareStatement( sql );
			
			for (int i = 0; i < valuesArr.length; i++) {
				Object[] values = valuesArr[ i ];
				for (int j = 0; j < values.length; j++) {
					st.setObject( j + 1, values[j] );
				}
				st.addBatch();
			}
			
			try {
				int[] results = st.executeBatch();
				for (int i = 0; i < results.length; i++) {
					result += results[ i ];
				}
				con.commit();
			} catch ( SQLException e ){
				e.printStackTrace();
				result = 0;
			}
			
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	
	/**
	 * 批量处理
	 * @param sql
	 * @param valuesArr
	 * @return
	 */
	public static int updateBatch( String sql, BatchExecutor executor ){
		if( sql == null || executor == null ){
			return 0;
		}
		
		int result = 0;
		try {
			Connection con = getConnection();
			
			PreparedStatement st = con.prepareStatement( sql );
			
			executor.execute( st );
			
			try {
				int[] results = st.executeBatch();
				for (int i = 0; i < results.length; i++) {
					result += results[ i ];
				}
				con.commit();
			} catch ( SQLException e ){
				e.printStackTrace();
				result = 0;
			}
			
			st.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
