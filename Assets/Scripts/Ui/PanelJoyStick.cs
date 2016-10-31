using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;





public class PanelJoystick : PanelBase {

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


    public static PanelJoystick inst;
    public static PanelJoystick getInstance()
    {
        if (inst == null)
            inst = new PanelJoystick();

        return inst;
    }


    public override void close()
    {
        base.close();
        inst = null;
    }


    private GameObject m_joy_gameObjcet;
    private RectTransform m_joy_transform;

	Vector3 originPos;


	float len = 50;


    public override void onBuild(Hashtable param)
    {
        m_joy_gameObjcet = gameObject.transform.FindChild("Circle").gameObject;
        m_joy_transform = m_joy_gameObjcet.transform as RectTransform;

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
        Vector2 local;
        RectTransformUtility.ScreenPointToLocalPointInRectangle(m_joy_transform, Input.mousePosition, MgrScene.uiCamera, out local);

        Vector3 delta = new Vector3(local.x, local.y);
		updateCirclePos (delta);
	}


	void OnUp(GameObject go, PointerEventData eventData)
	{
		m_joy_transform.localPosition = originPos;
	}


	void updateCirclePos(Vector3 delta)
	{
        //if(delta.magnitude > len)
        //    delta = Vector3.ClampMagnitude(delta, len);

        //m_joy_transform.localPosition = delta;



        m_joy_transform.localPosition = m_joy_transform.localPosition + delta;
        if (m_joy_transform.localPosition.magnitude > len)
        {
            m_joy_transform.localPosition = m_joy_transform.localPosition.normalized * len;
        }

        float rad = Mathf.Atan2(m_joy_transform.localPosition.y, m_joy_transform.localPosition.x);
        float rad2 = Mathf.Atan2(delta.y, delta.x);
        EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_UPDATE_JOYSTICK, rad);

        Tools.Log("rad:" + rad + " rad local:" + rad2);
	}
}
