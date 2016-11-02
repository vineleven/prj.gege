using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System.Collections.Generic;
using Global;



public class PanelBattle : PanelBase
{


    static PanelBattle m_inst = null;
    public static PanelBattle getInstance()
    {
        if (m_inst == null)
            m_inst = new PanelBattle();

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
        return MgrPanel.STYLE_COMMON;
    }


    public override string getResName()
    {
        return "PanelBattle";
    }


    Text m_scoreText;



    PanelBattle()
    {
        addEventCallback(EventId.UI_UPDATE_SCROE, onUpdateScore);
        startProcMsg();
    }


    bool m_back = false;
    public override void onBuild(Hashtable param)
    {
        transform.FindChild("BtnBack").GetComponent<Button>().onClick.AddListener(onClickBack);
        m_scoreText = transform.FindChild("Score").GetComponent<Text>();
    }



    void onClickBack()
    {
        if (!m_back)
        {
            m_back = true;
            MgrBattle.reqLeaveGame();
        }
    }


    void onUpdateScore(GameEvent e)
    {
        string text = e.getData() as string;
        m_scoreText.text = text;
    }


    void onClickCreate()
    {
        List<string> list = new List<string>();
        list.Add("1 Vs 1");
        list.Add("2 Vs 2");
        list.Add("3 Vs 3");
        list.Add("4 Vs 4");
        list.Add("5 Vs 5");

        MgrPanel.openOption(list, onClickOption);
    }


    void onClickOption(object o)
    {
        int index = (int)o;
        MgrPanel.openRoom(index);
        MgrNet.reqNewRoom(index);
        MgrPanel.openLoading();
    }

}
