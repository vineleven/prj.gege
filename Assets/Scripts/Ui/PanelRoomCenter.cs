using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using Global;
using System;
using UnityEngine.UI;



public class PanelRoomCenter : PanelBase {


    static PanelRoomCenter m_inst = null;
    public static PanelRoomCenter getInstance()
    {
        if (m_inst == null)
            m_inst = new PanelRoomCenter();

        return m_inst;
    }

    public override void close()
    {
        base.close();
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
        return "PanelRoomCenter";
    }



    GameObject m_templateItem;
    Transform m_content;
    ScrollRect m_grid;

    List<RoomItem> m_rooms = new List<RoomItem>();
    public override void onBuild(Hashtable param)
    {
        gameObject.transform.FindChild("BtnClose").GetComponent<Button>().onClick.AddListener(close);
        m_templateItem = gameObject.transform.FindChild("Scroll/RoomItem").gameObject;
        m_templateItem.SetActive(false);
        m_content = gameObject.transform.FindChild("Scroll/Viewport/Content").transform;
        m_grid = m_content.GetComponent<ScrollRect>();

        gameObject.transform.FindChild("Name").GetComponent<Text>().text = "Room Center";

        addEventCallback(EventId.UI_UPDATE_ROOM_CENTER, onUpdateRooms);
        startProcMsg();

        MgrNet.reqRoomCenter();
    }


    public void onUpdateRooms(GameEvent e)
    {
        ArrayList list = e.getData() as ArrayList;
        int index = 0;
        foreach (var data in list)
        {
            Hashtable rawData = data as Hashtable;
            if (rawData.Contains("name") && rawData.Contains("idx"))
            {
                string name = rawData["name"] as string;
                int idx = Convert.ToInt32(rawData["idx"]);
                string desc = rawData["desc"] as string;

                RoomItem item;
                if (index >= m_rooms.Count)
                {
                    var obj = GameObject.Instantiate(m_templateItem);
                    obj.transform.SetParent(m_content);
                    item = new RoomItem(obj, onClickJoin);
                    m_rooms.Add(item);
                }
                else
                {
                    item = m_rooms[index];
                }

                item.setRoomInfo(idx, name, desc);
                item.show();
                index++;
            }
        }

        for (int i = list.Count; i < m_rooms.Count; i++)
        {
            m_rooms[i].hide();
        }
    }


    void onClickJoin(object obj)
    {
        int roomId = (int)obj;
        Tools.Log("click room:" + roomId);

    }


    public class RoomItem
    {
        GameObject gameObject;
        CallbackWithParam m_callback;

        int m_roomId = -1;
        Text m_name;
        Text m_desc;
        public RoomItem(GameObject obj, CallbackWithParam callback)
        {
            gameObject = obj;
            m_callback = callback;

            obj.transform.FindChild("Button").GetComponent<Button>().onClick.AddListener(onClickJoin);
            m_name = obj.transform.FindChild("Name").GetComponent<Text>();
            m_desc = obj.transform.FindChild("Desc").GetComponent<Text>();
        }


        public void setRoomInfo(int idx, string  name, string desc)
        {
            m_roomId = idx;
            m_name.text = name;
            m_desc.text = desc;
        }


        void onClickJoin()
        {
            if (m_roomId != -1 && m_callback != null)
                m_callback(m_roomId);
        }


        public void hide()
        {
            gameObject.SetActive(false);
        }


        public void show()
        {
            gameObject.SetActive(true);
        }

    }
}
