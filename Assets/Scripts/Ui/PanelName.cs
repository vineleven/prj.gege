using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System.Collections.Generic;
using Global;




public class PanelName : PanelBase
{


    static PanelName m_inst = null;
    public static PanelName getInstance()
    {
        if (m_inst == null)
            m_inst = new PanelName();

        return m_inst;
    }


    public override void clean()
    {
        m_inst = null;
        m_nameCache.Clear();
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
        return "PanelName";
    }


    static RectTransform m_parent;
    private PanelName()
    {
        UpdateBehaviour com = gameObject.AddComponent<UpdateBehaviour>();
        com.setUpdateCallback(onUpdate);

        addEventCallback(EventId.UI_ADD_NAME, addName);
        startProcMsg();

        m_parent = transform;
    }


    GameObject m_nameTemplate;
    List<NameItem> m_nameCache = new List<NameItem>();
    public override void onBuild(Hashtable param)
    {
        m_nameTemplate = transform.FindChild("Name").gameObject;
        m_nameTemplate.SetActive(false);
    }


    public void onUpdate()
    {
        foreach (var item in m_nameCache)
        {
            item.update();
        }
    }


    public void addName(GameEvent e)
    {
        Hashtable data = e.getData() as Hashtable;
        string name = data["name"] as string;
        var node = data["node"] as Transform;

        var cloneItem = GameObject.Instantiate(m_nameTemplate);
        cloneItem.SetActive(true);
        cloneItem.transform.SetParent(transform, false);
        var item = new NameItem(cloneItem);
        item.setName(name);
        item.setTarget(node);
        m_nameCache.Add(item);
    }


    public void onClose(GameEvent e)
    {
        close();
    }


    class NameItem
    {
        Transform m_name;
        Text m_text;
        Transform m_target;

        public NameItem(GameObject obj)
        {
            m_name = obj.transform;
            m_text = m_name.FindChild("Text").GetComponent<Text>();
        }

        public void setName(string name)
        {
            m_text.text = name;
        }

        public void setTarget(Transform ta)
        {
            m_target = ta;
        }

        public void update()
        {
            if (m_target == null) return;

            Vector3 pos = MgrScene.battleCamera.WorldToScreenPoint(m_target.position);
            Vector2 namePos;
            RectTransformUtility.ScreenPointToLocalPointInRectangle(m_parent, pos, MgrScene.uiCamera, out namePos);
            m_name.localPosition = namePos;
        }
    }

}
