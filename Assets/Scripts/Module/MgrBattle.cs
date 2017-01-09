using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;
using Global;



public class MgrBattle : EventBehaviour
{


    const int STATE_IDLE = 0;
    const int STATE_DEMO = 1;
    const int STATE_LOADING = 2;
    const int STATE_GAME = 3;


    static int[] m_scores;

    static List<Player> m_players = new List<Player>();

    static List<GameItem> m_items = new List<GameItem>();

	static Player m_mainPlayer;
    static PlayerDemo m_playerDemo;

    static int m_playerIndex;

    static Map m_map;

    static int m_group;

    static FollowTarget m_follower;

    static int m_state = STATE_IDLE;

    // 这个时间只是为了计算loading
    private static long m_receiveStartGameTime = 0;
    private static long m_startGameTime = 0;

    public static long deltaTime = 0;

    public static long curTime = 0;



	void Awake(){
        addEventCallback(EventId.UI_UPDATE_JOYSTICK, onDragJoy);
        addEventCallback(EventId.MSG_UPDATE_PLAYER_POS, onUpdatePlayerPos);
        startProcMsg();
	}


	// Use this for initialization
	void Start () {
        MgrNet.registerCmd(Cmd.C2S_START_GAME, rspStartGame);
        MgrNet.registerCmd(Cmd.S2C_PLAYER_POS, rspPlayerPos);
        MgrNet.registerCmd(Cmd.S2C_COLLISION_RESULT, rspCollisionResult);
        MgrNet.registerCmd(Cmd.S2C_GAME_OVER, rspGameOver);
        MgrNet.registerCmd(Cmd.C2S_LEAVE_GAME, rspPlayerLeave);
        MgrNet.registerCmd(Cmd.S2C_RELIVE, rspPlayerRelive);
        MgrNet.registerCmd(Cmd.S2C_NEW_ITEM, rspNewItem);
        MgrNet.registerCmd(Cmd.S2C_ITEM_CHANGE, rspItemChange);

        m_follower = FollowTarget.Get(MgrScene.battleCamera.gameObject);
	}


    public override void onDestory()
    {
        clear();
    }


    public static void showDemoMap()
    {
        if(m_state != STATE_IDLE)
            return;

        clear();

        m_map = new Map();
        m_map.CreateMap();

        PlayerDemo player = new PlayerDemo("Player1");

        player.transform.SetParent(m_map.getGround());
        //player.set2Main();

        m_follower.SetTarget(player.transform);
        m_players.Add(player);
        curTime = getCurTime();
        m_playerDemo = player;

		setNextState(STATE_DEMO);
    }


    static void rspStartGame(Hashtable data)
    {
        EventDispatcher.getGlobalInstance().dispatchEvent(EventId.MSG_GAME_START);

        m_playerIndex = Convert.ToInt32(data["idx"]);
        m_group = Convert.ToInt32(data["group"]);
        m_startGameTime = Convert.ToInt64(data["time"]);
        m_receiveStartGameTime = MgrNet.getServerTime();

        if (m_startGameTime <= m_receiveStartGameTime)
            m_startGameTime = m_receiveStartGameTime + 1;

        ArrayList mapData = data["map"] as ArrayList;
        ArrayList players = data["list"] as ArrayList;
        float speed = Convert.ToSingle(data["speed"]);

        clear();

        m_map = new Map(mapData);
        m_map.CreateMap();

        int pos = Mathf.FloorToInt(mapData.Count / 2f);
        int size = Mathf.CeilToInt(mapData.Count / 2f);

        MgrScene.battleCamera.transform.position = new Vector3(pos, pos, -10);
        MgrScene.battleCamera.orthographicSize = size * 2;

        Hashtable pData;
        int group, idx;
        float x, y;
        string name;
        for (int i = 0; i < players.Count; i++)
        {
            pData = players[i] as Hashtable;
            group = Convert.ToInt32(pData["g"]);
            x = Convert.ToSingle(pData["x"]);
            y = Convert.ToSingle(pData["y"]);
            //speed = Convert.ToSingle(pData["s"]);
            idx = Convert.ToInt32(pData["i"]);
            name = pData["n"] as string;

            createPlayer(group, idx, x, y, speed, name);
        }


        setNextState(STATE_LOADING);
        curTime = getCurTime();
		
        MgrPanel.openLoading();
    }


    static void rspPlayerPos(Hashtable data)
    {
		long time = Convert.ToInt64(data["t"]);
		float x = Convert.ToSingle(data["x"]);
		float y = Convert.ToSingle(data["y"]);
		int group = Convert.ToInt32(data["g"]);
		int idx = Convert.ToInt32(data["i"]);

		foreach(var p in m_players)
		{
			if(p.getGroup() == group && p.getIndex() == idx)
			{
				if(group == m_mainPlayer.getGroup() && idx == m_mainPlayer.getIndex())
					Tools.LogError("---------- no no no!!!!!! group:" + group + " idx:" + idx);
				
				p.receiveNextPos(x, y, time);
			}
		}
    }


    static void rspCollisionResult(Hashtable data)
    {
        int wg = Convert.ToInt32(data["wg"]);
        int wi = Convert.ToInt32(data["wi"]);
        int lg = Convert.ToInt32(data["lg"]);
        int li = Convert.ToInt32(data["li"]);

        Tools.Log("player dead group:" + lg + " idx:" + li);

        m_scores[wg]++;
        string msg = string.Format("<color=red>{0}</color> : <color=blue>{1}</color>", m_scores[0], m_scores[1]);
        EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_UPDATE_SCROE, msg);

        foreach (var p in m_players)
        {
            if (p.getGroup() == wg && p.getIndex() == wi)
            {
                // 继续潇洒走一回
            }

            if (p.getGroup() == lg && p.getIndex() == li)
            {
                p.dead();
            }
        }
    }


    static void rspGameOver(Hashtable data)
    {
        string msg;
        int group = Convert.ToInt32(data["g"]);
        if (group == -1)
        {
            msg = "未能征服对手!";
        }
        else if (m_group == group)
        {
            msg = "你真棒!";
        }
        else
        {
            msg = "你真笨!";
        }

        EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_UPDATE_DEBUG_INFO, msg);

        clear();
        EventDispatcher.getGlobalInstance().dispatchEvent(EventId.MSG_GAME_OVER);
    }


    public static void reqLeaveGame()
    {
        if (m_state == STATE_GAME)
        {
            clear();
            MgrSocket.Send(Cmd.C2S_LEAVE_GAME, null);
            EventDispatcher.getGlobalInstance().dispatchEvent(EventId.MSG_GAME_OVER);
        }
    }


    static void rspPlayerLeave(Hashtable data)
    {
        int group = Convert.ToInt32(data["g"]);
        int idx = Convert.ToInt32(data["i"]);
        foreach (var p in m_players)
        {
            if (group == p.getGroup() && idx == p.getIndex())
            {
                p.dispose();
                m_players.Remove(p);
                break;
            }
        }
    }


    static void rspPlayerRelive(Hashtable data)
    {
        int group = Convert.ToInt32(data["g"]);
        int idx = Convert.ToInt32(data["i"]);
        float x = Convert.ToSingle(data["x"]);
        float y = Convert.ToSingle(data["y"]);

        foreach (var p in m_players)
        {
            if (p.getGroup() == group && p.getIndex() == idx)
            {
                p.relive(x, y);
                break;
            }
        }
    }


    static void rspNewItem(Hashtable data)
    {
        int type = Convert.ToInt32(data["t"]);
        float x = Convert.ToSingle(data["x"]);
        float y = Convert.ToSingle(data["y"]);
        int id = Convert.ToInt32(data["id"]);

        GameItem item = new GameItem("Item" + (type + 1), type, id, x, y);
        m_items.Add(item);
    }


    public void rspItemChange(Hashtable data)
    {
        //int type = Convert.ToInt32(data["t"]);
        int id = Convert.ToInt32(data["id"]);

        foreach (var item in m_items)
        {
            if (item.getId() == id)
            {
                item.dispose();
                m_items.Remove(item);
                EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_UPDATE_GROUP, item.getType());
                break;
            }
        }
    }


    // 清理战斗场景
    public static void clear()
    {
		setNextState(STATE_IDLE);

        m_scores = new int[]{0, 0};

        if (m_map != null)
        {
            m_map.dispose();
            m_map = null;
        }

        m_follower.SetTarget(null);

        m_playerDemo = null;
        m_mainPlayer = null;

        foreach (var p in m_players)
        {
            p.dispose();
        }

        m_players.Clear();

        foreach (var item in m_items)
        {
            item.dispose();
        }
        m_items.Clear();
    }


	static void setNextState(int state)
	{
		m_state = state;
	}


    static void createPlayer(int group, int idx, float x, float y, float speed, string name)
    {
        Player player = new Player("Player" + (group + 1), group, idx, x, y, speed);
        player.setName(name);
        if (idx == m_playerIndex && group == m_group)
        {
            player.set2Main();
            m_mainPlayer = player;
            m_follower.SetTarget(m_mainPlayer.transform);
        }

        m_players.Add(player);
    }

	
    public static long getCurTime()
    {
        if (m_state == STATE_DEMO)
        {
            return Tools.getCurTime();
        }
        else if(m_state == STATE_GAME || m_state == STATE_LOADING)
        {
            return MgrNet.getServerTime();
        }
        else
        {
            return -1;
        }
    }


    static void startGame()
    {
        MgrPanel.openJoyStick();
        EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_UPDATE_DEBUG_INFO, "");
        setNextState(STATE_GAME);
    }



    // Update is called once per frame
    void Update()
    {
        long now = getCurTime();
        deltaTime = now - curTime;
        curTime = now;

        if (m_state == STATE_IDLE) return;
        switch (m_state)
        {
            case STATE_IDLE:
                return;
            case STATE_LOADING:
                float p = (m_startGameTime - now) / (float)(m_startGameTime - m_receiveStartGameTime);
                p = 1 - p;

                EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_UPDATE_LOADING, p);
                if (p >= 1)
                    startGame();
                break;
        }
        

        foreach (var p in m_players)
        {
            p.update();
        }
    }


    public static Map getMap()
    {
        return m_map;
    }


    public static void onDragJoy(GameEvent e)
    {
        Vector3 dir = (Vector3)e.getData();
        //Tools.Log("--- rad:" + delta.ToString());
        if (m_state == STATE_DEMO)
        {
            m_playerDemo.setDir(dir);
            m_playerDemo.findNextPathFromUser();
        }
        else if (m_state == STATE_GAME)
        {
            if (m_mainPlayer == null)
                Tools.LogError("this is no main player");
            else
            {
                m_mainPlayer.setDir(dir);
                m_mainPlayer.findNextPath();
            }
        }
    }


    public static void onUpdatePlayerPos(GameEvent e)
    {
        Hashtable data = e.getData() as Hashtable;
        MgrSocket.Send(Cmd.C2S_PLAYER_POS, data);
    }
}


