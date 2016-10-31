using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;
using Global;



public class MgrBattle : MgrBase
{


    const int STATE_IDLE = 0;
    const int STATE_DEMO = 1;
    const int STATE_GAME = 0;



    static List<Player> m_players = new List<Player>();

	static Player m_mainPlayer;

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
        startProcMsg();
	}


	// Use this for initialization
	void Start () {
        MgrNet.registerCmd(Cmd.C2S_START_GAME, rspStartGame);
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

        m_follower.SetTarget(player.transform);

        m_players.Add(player);

        startGame(null);

        m_state = STATE_DEMO;
    }


    static void rspStartGame(Hashtable data)
    {
        // 多线程问题
        MgrTimer.callLaterTime(0, loadingGame, data);
    }


    // 清理战斗场景
    static void clear()
    {
        m_state = STATE_IDLE;

        if (m_map != null)
        {
            m_map.dispose();
            m_map = null;
        }

        m_follower.SetTarget(null);

        foreach (var p in m_players)
        {
            p.dispose();
        }

        m_players.Clear();
    }


    static void loadingGame(object obj)
    {
        Hashtable data = obj as Hashtable;
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
    }


    static void createPlayer(int group, int idx, float x, float y, float speed)
    {
        Player player = new Player("Player" + (group + 1), group, idx, x, y, speed);

        if (idx == m_playerIndex && group == m_group)
        {
            m_mainPlayer = player;
            m_follower.SetTarget(m_mainPlayer.transform);
        }

        m_players.Add(player);
    }

	
    static void changeUiToBattle()
    {
        MgrPanel.disposeAllPanel(MgrPanel.LAYER_UI);
        EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_CLOSE_LOADING);
    }


    static long getCurTime()
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
        m_state = STATE_GAME;
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
        float rad = (float)e.getData();
        Tools.Log("--- rad:" + rad);
        if (m_state == STATE_DEMO)
        {
            
        }
    }
}


