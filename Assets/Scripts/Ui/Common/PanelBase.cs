using UnityEngine;
using System.Collections;
using Global;

public abstract class PanelBase : EventHandler {

    public GameObject gameObject = null;
    public RectTransform transform = null;

    public abstract string getResName();

    public abstract void onBuild(Hashtable param);
    
    public abstract int getLayer();

    public abstract int getStyle();


    public PanelBase()
    {
        gameObject = MgrRes.newObject(getResName()) as GameObject;
        transform = gameObject.transform as RectTransform;
    }


    public void setParam(Hashtable param)
    {
        onBuild(param);
    }


    public virtual void show()
    {
        gameObject.SetActive(true);
    }


    public virtual void hide()
    {
        gameObject.SetActive(false);
    }


    public virtual void close()
    {
        stopProcMsg();
        MgrPanel.closePanel(this);
    }
}
