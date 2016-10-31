using UnityEngine;
using System.Collections;

public class Cmd
{
    // 获取server时间
    public const int C2S_TIME = 0;

    // 设置名字
    public const int C2S_NAME = 1;

    // 房间列表
    public const int C2S_ROOM_CENTER = 2;

    // 创建房间
    public const int C2S_NEW_ROOM = 3;

    // 房间信息
    public const int S2C_ROOM_INFO = 4;

    // 加入房间
    public const int C2S_JOIN_ROOM = 5;

    // 退出房间
    public const int C2S_LEVEL_ROOM = 6;

    // 开始游戏
    public const int C2S_START_GAME = 7;

    // 消息提示
    public const int S2C_SHOW_MSG = 100;

}
