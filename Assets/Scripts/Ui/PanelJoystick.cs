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


    public static PanelJoystick m_inst;
    public static PanelJoystick getInstance()
    {
        if (m_inst == null)
            m_inst = new PanelJoystick();

        return m_inst;
    }


    public override void clean()
    {
        m_inst = null;
    }


    private GameObject m_joy_gameObjcet;
    private RectTransform m_joy_transform;

	Vector3 originPos;


	const float MAX_LEN = 50;

    const float MIN_LEN = 5;


    Vector2 m_delta;


    public override void onBuild(Hashtable param)
    {
        //m_joy_transform = transform.FindChild("Node/Circle") as RectTransform;
        //originPos = m_joy_transform.localPosition;

        transform.FindChild("Node").gameObject.SetActive(false);


        var image = transform.FindChild("Image").gameObject;

        UIEventListener.Get(image).onDown = OnDown;
        UIEventListener.Get(image).onDrag = OnDrag;
        UIEventListener.Get(image).onUp = OnUp;

        UpdateBehaviour.Get(gameObject).setUpdateCallback(update);
    }


	void OnDown(GameObject go, PointerEventData eventData)
	{
        //updatePos();
        m_delta = Vector2.zero;
	}


	void OnDrag(GameObject go, PointerEventData eventData)
	{
        m_delta += eventData.delta;
	}


    void OnUp(GameObject go, PointerEventData eventData)
    {
        //m_joy_transform.localPosition = originPos;
        EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_UPDATE_JOYSTICK, new Vector3(m_delta.x, m_delta.y));
    }


    void update()
    {
        //if (m_bDrag)
        //    updatePos();
    }


    void updatePos()
    {
        Vector2 local;
        RectTransformUtility.ScreenPointToLocalPointInRectangle((RectTransform)m_joy_transform.parent, Input.mousePosition, MgrScene.uiCamera, out local);

        Vector3 curPos = new Vector3(local.x, local.y);
        updateCirclePos(curPos);
    }


	void updateCirclePos(Vector3 curPos)
	{
        float curLen = curPos.magnitude;
        if (curLen > MAX_LEN)
            curPos = Vector3.ClampMagnitude(curPos, MAX_LEN);

        m_joy_transform.localPosition = curPos;




        // 强制4个方向
        //switch (Map.getDir(Vector3.zero, curPos))
        //{
        //    case Map.DIR_UP:
        //        break;
        //    case Map.DIR_RIGHT:
        //        break;
        //    case Map.DIR_DOWN:
        //        break;
        //    case Map.DIR_LEFT:
        //        break;
        //    default:
        //        break;
        //}


        //if(curLen > MIN_LEN)
        //    EventDispatcher.getGlobalInstance().dispatchEvent(EventId.UI_UPDATE_JOYSTICK, curPos);
	}
}
