package gege.consts;

import gege.util.Mathf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public interface Global {
	
	// 每次起动会产生一个随机数，用于某些验证或者加密
	final public static int RANDOM_INT = Mathf.randomInt( 123456, 654321 );
		
	// server
	int GAME_SERVER_PORT = 51234;
	int LOGIN_SERVER_PORT = 6665;
	
	int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	
	int GAME_SERVER_ACCEPTOR_EVENT_LOOP_COUNT = 1;
	int GAME_SERVER_WORKER_EVENT_LOOP_COUNT = 2;
	
	int LOGIN_SERVER_ACCEPTOR_EVENT_LOOP_COUNT = 1;
	int LOGIN_SERVER_WORKER_EVENT_LOOP_COUNT = 2;
	
	
	// game
	int GAME_UPDATE_INTERVAL = 50;				// game更新间隔，主要影响请求响应
	int GAME_LOGIC_INTERVAL = 300;				// 逻辑（时间）更新间隔
	
	int GAME_MAX_PROCESS_REQUEST = 50;			// 请求处理上限
	int GAME_REQUEST_QUEUE_MAX_SIZE = 256;		// 游戏请求列队最大值
	
	
	String MSG_END_FLAG = "$";
	String MSG_KEY_CMD = "cmd";
	String MSG_KEY_DATA = "data";

	String SERVER_CONTENT_TYPE = "text/html";
	
	String DATE_FORMAT_STRING = "yyyy/MM/dd HH:mm:ss";

	String REQUEST_HEART_BEAT = "{\"cmd\":\"hb\"}";
	
	String CLASS_PATH_ROOT = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	
	// 匹配
	String REGEX_USER_NAME = "[^a-zA-Z0-9_]";
	String REGEX_PASSWORD = " ";
	String REGEX_PLAYER_NAME = "[^a-zA-Z0-9_\u4e00-\u9fa5]";
	
	String REGEX_CFG_SPLIT = ";";
	
	
	ByteBuf MSG_DELIMITER = Unpooled.copiedBuffer( MSG_END_FLAG.getBytes() );
	ByteBuf MSG_INVALID_JSON_STR = Unpooled.copiedBuffer( "invalid json str.".getBytes() );
	
	
	
	
	byte[][] MAP_DATA = {
			{},
			{}
	};
}
