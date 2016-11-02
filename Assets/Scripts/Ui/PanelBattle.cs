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
        addEventCallback(EventId.UI_UPDATE_GROUP, onUpdateGroup);
        startProcMsg();
    }


    bool m_back = false;
    Image m_image;
    public override void onBuild(Hashtable param)
    {
        transform.FindChild("BtnBack").GetComponent<Button>().onClick.AddListener(onClickBack);
        m_scoreText = transform.FindChild("Score").GetComponent<Text>();
        m_image = transform.FindChild("BG").GetComponent<Image>();
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


    void onUpdateGroup(GameEvent e)
    {
        int group = (int)e.getData();
        Color color;
        if(group == 0)
        {
            color = new Color(1, 0, 0, 0.25f);
        }
        else if (group == 1)
        {
            color = new Color(0, 0, 1, 0.25f);
        }
        else
        {
            color = new Color(0, 0, 0, 0);
        }

        m_image.color = color;
    }


}
