package gege.data;

public interface Config {
	
	public static String DATE_FORMAT_STRING = "yyyy/MM/dd_HH/mm/ss";
	
	/**
	 * GameServer timer更新间隔
	 */
	public static int GAME_SRERVER_UPDATE_INTERVAL = 1000 / 40;
	
	/**
	 * 服务器端口
	 */
	public static int SERVER_PORT = 51234;
	
	/**
	 * 服务器响应间隔(毫秒) 
	 */
	public static long SERVER_RESPONSE_INTERVAL = 2000;
	
	/**
	 * 超时时间
	 */
	public static long CLIENT_TIME_OUT = 2000;
	
	/**
	 * 超时检测间隔
	 */
	public static int SERVER_TIME_OUT_INTERVAL = 50;
	
	/**
	 * 根目录
	 */
	public static String SERVER_FILE_ROOT = "D:/mygame/";
	
	
	public static String SERVER_CONTENT_TYPE = "text/html";

	
	public static String SERVER_ENCODING = "UTF-8";//"ASCII";
}
