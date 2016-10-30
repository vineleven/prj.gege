using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System.Collections.Generic;
using Global;



public class PanelMain : PanelBase
{


    static PanelMain m_inst = null;
    public static PanelMain getInstance()
    {
        if (m_inst == null)
            m_inst = new PanelMain();

        return m_inst;
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
        return "PanelMain";
    }


    public override void onBuild(Hashtable param)
    {
        gameObject.transform.FindChild("BtnJoin").GetComponent<Button>().onClick.AddListener(onClickJoin);
        gameObject.transform.FindChild("BtnCreate").GetComponent<Button>().onClick.AddListener(onClickCreate);
    }



    void onClickJoin()
    {
        MgrPanel.openRoomCenter();
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
    }

    public override void close()
    {
        base.close();
        m_inst = null;
    }
}
