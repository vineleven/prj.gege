﻿using UnityEngine;
using System.Collections;

public enum EventId {
    MSG_ERROR_CODE,
    
    MSG_DISCONNECTED,
    MSG_CONNECTED,

    MSG_GAME_START,

    MSG_UPDATE_PLAYER_POS,

    UI_UPDATE_PING,
    UI_UPDATE_DEBUG_INFO,

    UI_UPDATE_ROOM_CENTER,
    UI_UPDATE_ROOM_INFO,

    UI_UPDATE_JOYSTICK,

    UI_CLOSE_LOADING,
}
