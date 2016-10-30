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

    public override void close()
    {
        base.close();
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
        return "PanelDialog";
    }


    public override void onBuild(Hashtable param)
    {
        //EventDispatcher.getGlobalInstance().addListener(Events.UI_CLOSE_LOADING, onClose);
    }


}
