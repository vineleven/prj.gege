package gege.consts;

public interface Cmd {
	// 获取server时间
	int C2S_TIME = 0;
	
	// 设置名字
	int C2S_NAME = 1;
	
	// 房间列表
	int C2S_ROOM_CENTER = 2;
	
	// 创建房间
	int C2S_NEW_ROOM = 3;
	
	// 房间信息
	int S2C_ROOM_INFO = 4;
	
	// 加入房间
	int C2S_JOIN_ROOM = 5;
	
	// 离开房间
	int C2S_LEVEL_ROOM = 6;
	
	// 开始游戏
	int C2S_START_GAME = 7;
	
	
	
	
	int S2C_SHOW_MSG = 100;
	
//	int S2C_TIME = 100;
//	int S2C_NEW_ROOM = 101;
}
