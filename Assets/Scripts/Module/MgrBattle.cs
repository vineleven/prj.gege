using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;
using Global;



public class MgrBattle : EventBehaviour
{


    const int STATE_IDLE = 0;
    const int STATE_DEMO = 1;
    const int STATE_GAME = 2;



    static List<Player> m_players = new List<Player>();

	static Player m_mainPlayer;
    static PlayerDemo m_playerDemo;

    static int m_playerIndex;

    static Map m_map;

    static int m_group;

    static FollowTarget m_follower;

    static int m_state = STATE_IDLE;



    public static long deltaTime = 0;

    public static long curTime = 0;



	void Awake(){
        addEventCallback(EventId.MSG_CONNECTED, onConnected);
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

        m_follower = FollowTarget.Get(MgrScene.battleCamera.gameObject);
	}


    public override void onDestory()
    {
        clear();
    }


    void onConnected(GameEvent e)
    {
        if (m_state == STATE_IDLE)
            showDemoMap();
    }


    public static void showDemoMap()
    {
        clear();

        m_map = new Map();
        m_map.CreateMap();

        PlayerDemo player = new PlayerDemo("Player1");

        player.transform.SetParent(m_map.getGround());
        //player.set2Main();

        m_follower.SetTarget(player.transform);

        m_players.Add(player);

        startGame(null);

        m_playerDemo = player;

		setNextState(STATE_DEMO);
    }


    static void rspStartGame(Hashtable data)
    {
        m_playerIndex = Convert.ToInt32(data["idx"]);
        m_group = Convert.ToInt32(data["group"]);

        long startTime = Convert.ToInt64(data["time"]);
        ArrayList mapData = data["map"] as ArrayList;
        ArrayList players = data["list"] as ArrayList;

        clear();

        m_map = new Map(mapData);
        m_map.CreateMap();

        Hashtable pData;
        int group, idx;
        float x, y, speed;
        for (int i = 0; i < players.Count; i++)
        {
            pData = players[i] as Hashtable;
            group = Convert.ToInt32(pData["g"]);
            x = Convert.ToSingle(pData["x"]);
            y = Convert.ToSingle(pData["y"]);
            speed = Convert.ToSingle(pData["s"]);
            idx = Convert.ToInt32(pData["i"]);

            createPlayer(group, idx, x, y, speed);
        }

        changeUiToBattle();

        MgrTimer.callLaterTime((int)(startTime - Tools.getCurTime()), startGame);
		EventDispatcher.getGlobalInstance().dispatchEvent(EventId.MSG_GAME_START);
		setNextState(STATE_GAME);
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

        foreach (var p in m_players)
        {
            if (p.getGroup() == wg && p.getIndex() == wi)
            {
            }

            if (p.getGroup() == lg && p.getIndex() == li)
            {
                p.dead();
            }
        }
    }


    static void rspGameOver(Hashtable data)
    {
        EventDispatcher.getGlobalInstance().dispatchEvent(EventId.MSG_GAME_OVER);
    }


    // 清理战斗场景
    static void clear()
    {
		setNextState(STATE_IDLE);

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
    }


	static void setNextState(int state)
	{
		m_state = state;
	}


    static void createPlayer(int group, int idx, float x, float y, float speed)
    {
        Player player = new Player("Player" + (group + 1), group, idx, x, y, speed);

        if (idx == m_playerIndex && group == m_group)
        {
            player.set2Main();
            m_mainPlayer = player;
            m_follower.SetTarget(m_mainPlayer.transform);
			EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_UPDATE_DEBUG_INFO, "g:" + group + " i:" + idx);
        }

        m_players.Add(player);
    }

	
    static void changeUiToBattle()
    {
        //MgrPanel.disposeAllPanel(MgrPanel.LAYER_UI);
        //EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_CLOSE_LOADING);
    }


    public static long getCurTime()
    {
        if (m_state == STATE_DEMO)
        {
            return Tools.getCurTime();
        }
        else if(m_state == STATE_GAME)
        {
            return MgrNet.getServerTime();
        }
        else
        {
            return -1;
        }
    }


    static void startGame(object o)
    {
        curTime = getCurTime();
    }



    // Update is called once per frame
    void Update()
    {
        if (m_state == STATE_IDLE) return;

        long now = getCurTime();
        deltaTime = now - curTime;
        curTime = now;

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
                m_mainPlayer.findNextPath(true);
            }
        }
    }


    public static void onUpdatePlayerPos(GameEvent e)
    {
        Hashtable data = e.getData() as Hashtable;
        MgrSocket.Send(Cmd.C2S_PLAYER_POS, data);
    }
}


