using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System.Collections.Generic;
using Global;




public class PanelLoading : PanelBase
{


    static PanelLoading m_inst = null;
    public static PanelLoading getInstance()
    {
        if (m_inst == null)
            m_inst = new PanelLoading();

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
        return "PanelLoading";
    }

    private PanelLoading()
    {
        addEventCallback(EventId.UI_CLOSE_LOADING, onClose);
        startProcMsg();
    }


    public override void onBuild(Hashtable param)
    {
    }


    public void onClose(GameEvent e)
    {
        close();
    }

}
