using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System.Collections.Generic;
using Global;




public class PanelReady : PanelBase
{


    static PanelReady m_inst = null;
    public static PanelReady getInstance()
    {
        if (m_inst == null)
            m_inst = new PanelReady();

        return m_inst;
    }


    public override void clean()
    {
        m_inst = null;
    }


    public override int getLayer()
    {
        return MgrPanel.LAYER_DIALOG;
    }


    public override int getStyle()
    {
        return MgrPanel.STYLE_COMMON;
    }


    public override string getResName()
    {
        return "PanelReady";
    }

    private PanelReady()
    {
        //addEventCallback(EventId.UI_CLOSE_LOADING, onClose);
        //startProcMsg();
    }


    public override void onBuild(Hashtable param)
    {
    }


    public void onClose(GameEvent e)
    {
        close();
    }

}
