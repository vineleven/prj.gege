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
	int C2S_LEAVE_ROOM = 6;
	
	// 开始游戏
	int C2S_START_GAME = 7;
	
	// 玩家位置更新
    int C2S_PLAYER_POS = 8;
    
    // 广播位置
    int S2C_PLAYER_POS = 9;
    
    // 广播碰撞结果
    int S2C_COLLISION_RESULT = 10;
    
    // 游戏结束
    int S2C_GAME_OVER = 11;
    
    // 主动退出游戏
    int C2S_LEAVE_GAME = 12;
    
    // 复活
    int S2C_RELIVE = 13;
    
	
	// 返回错误消息
	int S2C_SHOW_MSG = 100;
	
	// GM命令
    int C2S_GM = 200;
}
