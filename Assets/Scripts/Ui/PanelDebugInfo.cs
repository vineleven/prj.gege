using UnityEngine;
using System.Collections;
using Global;
using UnityEngine.UI;



public class PanelDebugInfo : PanelBase
{


    static PanelDebugInfo m_inst = null;
    public static PanelDebugInfo getInstance()
    {
        if (m_inst == null)
            m_inst = new PanelDebugInfo();

        return m_inst;
    }


    public override void clean()
    {
        m_inst = null;
    }


    public override int getLayer()
    {
        return MgrPanel.LAYER_TOP;
    }


    public override int getStyle()
    {
        return MgrPanel.STYLE_COMMON;
    }


    public override string getResName()
    {
        return "PanelDebugInfo";
    }


    PanelDebugInfo()
    {
        addEventCallback(EventId.UI_UPDATE_PING, updatePing);
        addEventCallback(EventId.UI_UPDATE_DEBUG_INFO, updateDebugInfo);
        startProcMsg();
    } 


    Text m_ping;
    Text m_debug;
    Text m_fps;
    public override void onBuild(Hashtable param)
    {
        var com = gameObject.transform.FindChild("Button").GetComponent<Button>();
        com.onClick.AddListener(OnClickButton);

        m_ping = gameObject.transform.FindChild("Ping").GetComponent<Text>();
        m_debug = gameObject.transform.FindChild("Debug").GetComponent<Text>();
        m_fps = gameObject.transform.FindChild("Fps").GetComponent<Text>();
    }


    void OnClickButton()
    {
        if(!MgrSocket.connected())
            EventDispatcher.getGlobalInstance().dispatchEvent(EventId.MSG_RETRY_CONNECT);
    }


    void updatePing(GameEvent obj)
    {
        updateUiPing(obj.getData());
    }


    void updateUiPing(object obj)
    {
        long ping = (long)obj;
        m_ping.text = "ping:" + ping;
        if (ping < 50)
            m_ping.color = Color.green;
        else if (ping < 100)
            m_ping.color = Color.yellow;
        else
            m_ping.color = Color.red;
    }


    void updateDebugInfo(GameEvent e)
    {
        updateUiDebugInfo(e.getData());
    }


    void updateUiDebugInfo(object obj)
    {
        string text = obj as string;
        m_debug.text = text;
    }





}
