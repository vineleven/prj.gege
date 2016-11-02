using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System.Collections.Generic;
using Global;
using System;


public class PanelRoom : PanelBase
{


    static PanelRoom m_inst = null;
    public static PanelRoom getInstance()
    {
        if (m_inst == null)
            m_inst = new PanelRoom();

        return m_inst;
    }


    public override void clean()
    {
        m_inst = null;
    }


    public override int getLayer()
    {
        return MgrPanel.LAYER_UI;
    }


    public override int getStyle()
    {
        return MgrPanel.STYLE_FULL;
    }


    public override string getResName()
    {
        return "PanelRoom";
    }



    List<PlayerItem>[] m_groups;


    Text m_name;
    bool m_data_prepared = false;
    bool m_player_all_in = false;
    int m_sideCount;
    int m_roomIdx = -1;
    bool m_bHost = false;

    GameObject m_btnStart;


    PanelRoom()
    {
        addEventCallback(EventId.UI_UPDATE_ROOM_INFO, updateRoomPlayer);
        startProcMsg();
    }


    public override void onBuild(Hashtable param)
    {
        //EventDispatcher.getGlobalInstance().addListener(Events.UI_CLOSE_LOADING, onClose);

        transform.FindChild("BtnClose").GetComponent<Button>().onClick.AddListener(onClickClose);
        m_btnStart = transform.FindChild("BtnStart").gameObject;
        m_btnStart.GetComponent<Button>().onClick.AddListener(onCLickStart);

        m_name = transform.FindChild("Name").GetComponent<Text>();

        var contents = new Transform[2];
        for (int i = 0; i < contents.Length; i++)
        {
            contents[i] = transform.FindChild("Viewport/Content" + (i + 1));
        }

        var objItem = new GameObject[contents.Length];
        for (int i = 0; i < contents.Length; i++)
        {
            objItem[i] = contents[i].FindChild("Button").gameObject;
        }


        m_sideCount = (int)param["sideCount"];
        int style = (int)param["style"];

        if (m_sideCount > 5)
        {
            Tools.LogWarn("side count invalid:" + m_sideCount);
            return;
        }

        m_groups = new List<PlayerItem>[contents.Length];
        for (int i = 0; i < m_groups.Length; i++)
        {
            m_groups[i] = new List<PlayerItem>();
            m_groups[i].Add(new PlayerItem(objItem[i], i, onClickJoinGroup));
        }

        // 剩下的克隆
        for (int i = 1; i < m_sideCount; i++)
        {
            for (int j = 0; j < m_groups.Length; j++)
            {
                var item = GameObject.Instantiate(objItem[j]);
                item.transform.SetParent(contents[j]);
                m_groups[j].Add(new PlayerItem(item, j, onClickJoinGroup));
            }
        }

        for (int i = 0; i < m_sideCount; i++)
        {
            foreach(var group in m_groups)
                group[i].show().setPlayerInfo("loading..", false);
        }
    }


    void onClickClose()
    {
        close();
        MgrNet.reqLeaveRoom();
    }


    void onCLickStart()
    {
        if (m_player_all_in)
        {
            MgrNet.reqStartGame();
            //MgrPanel.openLoading();
        }
        else
        {
            MgrPanel.openDialog("player not enough.");
        }
    }


    void onClickJoinGroup(object obj)
    {
        if(!m_data_prepared)
            return;

        int group = (int)obj;
        MgrNet.reqJoinRoom(m_roomIdx, group);
    }


    void updateRoomPlayer(GameEvent e)
    {
        Hashtable data = e.getData() as Hashtable;
        //string name = data["name"] as string;
        ArrayList players = data["list"] as ArrayList;
        m_roomIdx = Convert.ToInt32(data["idx"]);

        m_data_prepared = true;

        //m_name.text = name;

        clear();
        int[] groupIdx = new int[m_groups.Length];
        // 这里是信任数据的
        for (int i = 0; i < players.Count; i++)
        {
            Hashtable pData = players[i] as Hashtable;
            var pName = pData["name"] as string;
            var group = Convert.ToInt32(pData["group"]);
            var bHost = Convert.ToBoolean(pData["host"]);

            var index = groupIdx[group]++;
            m_groups[group][index].setPlayerInfo(pName, bHost);
            if (bHost)
            {
                m_name.text = pName;
                if (pName == MgrNet.playerName)
                    m_bHost = true;
            }
        }

        if(m_sideCount * 2 == players.Count)
            m_player_all_in = true;
        else
            m_player_all_in = false;

        if (m_bHost)
            m_btnStart.SetActive(true);
        else
            m_btnStart.SetActive(false);

        EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_CLOSE_LOADING);
    }


    void clear()
    {
        for (int i = 0; i < m_sideCount; i++)
        {
            foreach (var group in m_groups)
                group[i].show().clear();
        }

        m_bHost = false;
    }





    public class PlayerItem
    {
        GameObject m_gameObject;
        bool empty = true;
        Text m_text;
        int m_group = 0;
        GameObject m_sign;
        CallbackWithParam m_callback;
        public PlayerItem(GameObject obj, int group, CallbackWithParam callback)
        {
            m_gameObject = obj;
            m_group = group;
            m_callback = callback;
            m_sign = obj.transform.FindChild("Sign").gameObject;

            obj.GetComponent<Button>().onClick.AddListener(onClick);
            m_text = obj.transform.FindChild("Text").GetComponent<Text>();
            hide();
            m_sign.SetActive(false);
        }



        public void setPlayerInfo(string name, bool bHost)
        {
            m_text.text = name;
            empty = false;
            if (bHost)
                m_sign.SetActive(true);
        }


        public void clear()
        {
            m_text.text = "Join";
            empty = true;
            m_sign.SetActive(false);
        }


        public void onClick()
        {
            if (!empty)
                return;

            if (m_callback != null)
                m_callback(m_group);
        }


        public PlayerItem show()
        {
            m_gameObject.SetActive(true);
            return this;
        }


        public void hide()
        {
            m_gameObject.SetActive(false);
        }
    }
}
