using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using Global;
using Convert = System.Convert;
using Exception = System.Exception;


public class MgrNet : MonoBehaviour
{
    public static void registerCmds()
    {
        registerCmd(Cmd.C2S_TIME, rspTime);
        registerCmd(Cmd.C2S_ROOM_CENTER, rspRoomCenter);
        registerCmd(Cmd.S2C_ROOM_INFO, rspRoomInfo);
        
    }


    public static string playerName = "playerName";

    public static void reqSetName(string name)
    {
        Hashtable data = new Hashtable();
        data["name"] = name;
        MgrSocket.Send(Cmd.C2S_NAME, data);
        playerName = name;
    }



    public static void syncServerTime()
    {
        if (MgrSocket.connected())
        {
            Hashtable data = new Hashtable();
            data["cTime"] = Tools.getCurTime();
            MgrSocket.Send(Cmd.C2S_TIME, data);
        }

        MgrTimer.callLaterTime(5000, obj => syncServerTime());
    }


    static long m_pingTimeCur = 0;
    static long m_pingTimeMin = long.MaxValue;
    // 相对服务器的时间延迟
    static long m_clientTimeModify = 0;
    static void rspTime(Hashtable data)
    {
        if (data.Contains("sTime"))
        {
            long clientStartTime = Convert.ToInt64(data["cTime"]);
            long serverReceiveTime = Convert.ToInt64(data["sTime"]);
            long clientEndTime = Tools.getCurTime();

            m_pingTimeCur = clientEndTime - clientStartTime;

            if (m_pingTimeCur < m_pingTimeMin)
            {
                m_pingTimeMin = m_pingTimeCur;
                long clientArrivedTime = (clientStartTime + clientEndTime) / 2;
                m_clientTimeModify = serverReceiveTime - clientArrivedTime;
            }

            EventDispatcher.getGlobalInstance().dispatchUiEvent(EventId.UI_UPDATE_PING, m_pingTimeCur);

            //Tools.Log("Time ping:" + m_pingTimeCur + " min ping:" + m_pingTimeMin);
            //Tools.Log("curTime:" + getServerTime());
            //Tools.Log("ServerTime:" + (clientEndTime - m_clientTimeModify));
        }
    }


    public static long getServerTime()
    {
        return Tools.getCurTime() + m_clientTimeModify;
    }



    public static void reqRoomCenter()
    {
        MgrSocket.Send(Cmd.C2S_ROOM_CENTER);
    }


    static void rspRoomCenter(Hashtable data)
    {
        if(!data.Contains("list"))
            return;

        ArrayList list = data["list"] as ArrayList;

        EventDispatcher.getGlobalInstance().dispatchUiEvent(EventId.UI_UPDATE_ROOM_CENTER, list);
    }


    public static void reqNewRoom(int count, int style = Consts.STYLE_CUSTOM)
    {
        Hashtable data = new Hashtable();
        data.Add("style", style);
        data.Add("sideCount", count);

        MgrSocket.Send(Cmd.C2S_NEW_ROOM, data);
    }


    static void rspRoomInfo(Hashtable data)
    {
        if (data.ContainsKey("list"))
        {
            //ArrayList list = data["list"] as ArrayList;
            EventDispatcher.getGlobalInstance().dispatchUiEvent(EventId.UI_UPDATE_ROOM_INFO, data);
        }
    }


    public static void reqJoinRoom(int roomIdx, int group = -1)
    {
        Hashtable data = new Hashtable();
        data["idx"] = roomIdx;
        data["group"] = group;

        MgrSocket.Send(Cmd.C2S_JOIN_ROOM, data);
    }
































    /************************************************************/
    EventHandler m_eventHandler;
    // Use this for initialization
    void Awake() {
        m_eventHandler = new EventHandler();
        m_eventHandler.addEventCallback(EventId.GLOBAL_RESPONSE, responseCallback);
        m_eventHandler.startProcMsg();

        registerCmds();
	}


    void Start()
    {
        syncServerTime();
    }

    // Update is called once per frame
    void Update()
    {
    }


    void responseCallback(GameEvent e)
    {
        Hashtable rsp = e.getData() as Hashtable;
        if (rsp.Contains(Consts.MSG_KEY_CMD))
        {
            try
            {
                int cmd = Convert.ToInt32(rsp[Consts.MSG_KEY_CMD]);
                if (m_responses.ContainsKey(cmd))
                    if (rsp.ContainsKey(Consts.MSG_KEY_DATA))
                    {
                        Hashtable data = rsp[Consts.MSG_KEY_DATA] as Hashtable;
                        m_responses[cmd].Invoke(data);
                    }
            }
            catch (Exception ex)
            {
                Tools.LogError("responseCallback:" + ex.Message);
            }
        }
    }


    public delegate void OnResponse(Hashtable data);
    static Dictionary<int, OnResponse> m_responses = new Dictionary<int, OnResponse>();
    public static void registerCmd(int cmd, OnResponse callback)
    {
        if (m_responses.ContainsKey(cmd))
            Tools.LogWarn("register cmd duplicate cmd:" + cmd);
        else
            m_responses.Add(cmd, callback);
    }


    void OnDestroy()
    {
        if (m_eventHandler != null)
            m_eventHandler.stopProcMsg();

        m_responses.Clear();
    }


    



}
