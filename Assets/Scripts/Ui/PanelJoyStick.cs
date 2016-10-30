using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;





public class PanelJoyStick : PanelBase {

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
        return "PanelJoyStick";
    }


    public static PanelJoyStick inst;
    public static PanelJoyStick getInstance()
    {
        if (inst == null)
            inst = new PanelJoyStick();

        return inst;
    }


    public override void close()
    {
        base.close();
        inst = null;
    }


    private GameObject m_joy_gameObjcet;
    private Transform m_joy_transform;

	Vector3 originPos;

	public delegate void OnDragDelegate(Vector3 dir);

	public OnDragDelegate onDragListener = null;

	float len = 50;


    public override void onBuild(Hashtable param)
    {
        m_joy_gameObjcet = gameObject.transform.FindChild("Circle").gameObject;
        m_joy_transform = m_joy_gameObjcet.transform;

		originPos = m_joy_transform.localPosition;

        UIEventListener.Get(m_joy_gameObjcet).onDown = OnDown;
        UIEventListener.Get(m_joy_gameObjcet).onDrag = OnDrag;
        UIEventListener.Get(m_joy_gameObjcet).onUp = OnUp;
    }


	void OnDown(GameObject go, PointerEventData eventData)
	{
		Vector2 pos = eventData.position;
		Vector3 nowPos = MgrScene.uiCamera.ScreenToWorldPoint(new Vector3 (pos.x, pos.y, 0));
        m_joy_transform.position = nowPos;

		updateCirclePos (Vector3.zero);
	}


	void OnDrag(GameObject go, PointerEventData eventData)
	{
		Vector2 pos = eventData.delta;
		Vector3 delta = new Vector3 (pos.x, pos.y);
		updateCirclePos (delta);
	}


	void OnUp(GameObject go, PointerEventData eventData)
	{
		m_joy_transform.localPosition = originPos;
	}


	void updateCirclePos(Vector3 delta)
	{
        m_joy_transform.localPosition = m_joy_transform.localPosition + delta;
        if (m_joy_transform.localPosition.magnitude > len) 
		{
            m_joy_transform.localPosition = m_joy_transform.localPosition.normalized * len;
		}

		if (onDragListener != null)
            onDragListener(m_joy_transform.localPosition.normalized);
	}
}
