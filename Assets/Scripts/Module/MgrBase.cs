using UnityEngine;
using System.Collections;
using Global;


public abstract class MgrBase : MonoBehaviour {


    private EventHandler m_eventHandler = new EventHandler();

    public void addEventCallback(EventId eventId, OnEvent callback)
    {
        m_eventHandler.addEventCallback(eventId, callback);
    }


    public void startProcMsg()
    {
        m_eventHandler.startProcMsg();
    }


    public void stopProcMsg()
    {
        m_eventHandler.stopProcMsg();
    }


    void OnDestroy()
    {
        stopProcMsg();
        onDestory();
    }


    public abstract void onDestory();
}
