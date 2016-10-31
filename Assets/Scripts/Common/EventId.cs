using UnityEngine;
using System.Collections;

public enum EventId {
    GLOBAL_RESPONSE,
    
    MSG_START_GAME,
    MSG_DISCONNECTED,
    MSG_CONNECTED,

    UI_UPDATE_PING,
    UI_UPDATE_DEBUG_INFO,

    UI_UPDATE_ROOM_CENTER,
    UI_UPDATE_ROOM_INFO,

    UI_UPDATE_JOYSTICK,

    UI_CLOSE_LOADING,
}
