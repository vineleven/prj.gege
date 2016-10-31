using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;

public class MgrBattle : MonoBehaviour {



	static Player m_mainPlayer;


    static int m_playerIndex;

    static Map m_map;


    static int m_group;


	void Awake(){
        //mainPlayer = GameObject.Find ("MainPlayer");
	}


	// Use this for initialization
	void Start () {
        MgrNet.registerCmd(Cmd.C2S_START_GAME, rspStartGame);
        showDemoMap();
	}


    void OnDestroy()
    {
        m_map.dispose();
    }


    static void showDemoMap()
    {
        if (m_map != null)
            m_map.dispose();

        m_map = new Map();
        m_map.CreateMap();

        GameObject player = MgrRes.newObject("Player1") as GameObject;
        player.transform.SetParent(m_map.getGround());

        FollowTarget.Get(MgrScene.battleCamera.gameObject).SetTarget(player.transform);
    }


    static void rspStartGame(Hashtable data)
    {
        // 多线程问题
        MgrTimer.callLaterTime(0, startGame, data);
    }


    static void startGame(object obj)
    {
        Hashtable data = obj as Hashtable;
        m_playerIndex = Convert.ToInt32(data["idx"]);
        m_group = Convert.ToInt32(data["group"]);

        long startTime = Convert.ToInt64(data["time"]);
        ArrayList mapData = data["map"] as ArrayList;
        ArrayList players = data["list"] as ArrayList;

        if (m_map != null)
            m_map.dispose();
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

        createUi();
    }


    static List<Player> m_players = new List<Player>();

    static void createPlayer(int group, int idx, float x, float y, float speed)
    {
        Player player = new Player("Player" + (group + 1), group, idx);
        player.setPosition(x, y);
        player.setSpeed(speed);

        if (idx == m_playerIndex && group == m_group)
        {
            m_mainPlayer = player;
            FollowTarget.Get(MgrScene.battleCamera.gameObject).SetTarget(m_mainPlayer.transform);
        }


        m_players.Add(player);
    }


	
	// Update is called once per frame
	void Update () {
	    
	}


    static void createUi()
    {
        MgrPanel.disposeAllPanel(MgrPanel.LAYER_UI);
        EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_CLOSE_LOADING);

    }



}
