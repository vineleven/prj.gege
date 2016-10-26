package gege.consts;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;


public interface Global {
	
	
	// server
	int GAME_SERVER_PORT = 6666;
	int LOGIN_SERVER_PORT = 6665;
	
	int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	
	int GAME_SERVER_ACCEPTOR_EVENT_LOOP_COUNT = 1;
	int GAME_SERVER_WORKER_EVENT_LOOP_COUNT = 2;
	
	int LOGIN_SERVER_ACCEPTOR_EVENT_LOOP_COUNT = 1;
	int LOGIN_SERVER_WORKER_EVENT_LOOP_COUNT = 2;
	
	
	// sql
	int MYSQL_UPDATE_COUNT_PER_CYCLE = 1000;	// 每次更新条数
	int MYSQL_UPDATE_COUNT_PER_CONN = 20;		// 每次连写写入条数
	int MYSQL_UPDATE_INTERVAL = 1000 / 2;		// 数据更新间隔（可以短一点，不会增加update量，尽量保证切换cache期间更新完毕）
	int MYSQL_INSERT_INTERVAL = 1000;			// sql insert间隔
	int MYSQL_UPDATE_CACHE_INTERVAL = 1000 * 5;	// 更新cache间隔（交换cache）
	
	
	// game
	int GAME_UPDATE_INTERVAL = 50;				// game更新间隔，主要影响请求响应
	int GAME_LOGIC_INTERVAL = 300;				// 逻辑（时间）更新间隔
	int GAME_PROCESS_REQUEST_INTERVAL = 50;		// 请求处理间隔
	int GAME_MAX_PROCESS_REQUEST = 50;			// 请求处理上限
	int GAME_REQUEST_QUEUE_MAX_SIZE = 128;		// 游戏请求列队最大值
	int GAME_HERO_MAX_SIZE = 20;		        // 英雄最大数量  TODO: 最大数量可能会从某个养成表上获取
	int GAME_MAP_R = 8;                         // 地图半径
	int GAME_MAP_MAX_SIZE = GAME_MAP_R * 2 + 1; // 地图最大尺寸
	int GAME_RES_UPDATE_INTERVAL = 5;			// 资源更新周期(秒)(受world更新频率影响)
	
	int GAME_MAX_PRODUCE_QUEUE_SIZE = 3;		// 最大生产列队数
	
	
	// state
	int ST_CITY_CREATE = 0;
	int ST_CITY_NORMAL = 1;
	
	int ST_UP_IDLE 		= 0; // 闲置
	int ST_UP_PRODUCE 	= 1; // 生产中
	int ST_UP_CD 		= 2; // cd中
	int ST_UP_UPABLE	= 3; // 可升级状态
	
	// up
	int UP_REQ_ITEM_MAX_COUNT = 2;
	int UP_DEP_UPIDS_MAX_COUNT = 5;
	
	int UP_LEVEL_LIMIT_MAX = 200;
	
	
	int TECH_UP_SCOPE_DEPTH = 0;		// 科技up深度
	int CITY_UP_SCOPE_DEPTH = 1;		// 城市up深度
	
	
	
	
	
	
	
	
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
	
	String DEFAULT_EMAIL = "default@qq.com";
	
	
	
	
	ByteBuf MSG_DELIMITER = Unpooled.copiedBuffer( MSG_END_FLAG.getBytes() );
	ByteBuf MSG_INVALID_JSON_STR = Unpooled.copiedBuffer( "invalid json str.".getBytes() );
	
	
}
