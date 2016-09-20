using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;





public class JoyStick : MonoBehaviour {

	// Use this for initialization

	Vector3 originPos;

	public delegate void OnDragDelegate(Vector3 dir);

	public OnDragDelegate onDragListener = null;

	float len = 50;

	void Start () {
		originPos = transform.position;

		UIEventListener.Get (gameObject).onDown = OnDown;
		UIEventListener.Get (gameObject).onDrag = OnDrag;
		UIEventListener.Get (gameObject).onUp = OnUp;
	}


	void OnDown(GameObject go, PointerEventData eventData)
	{
		Vector2 pos = eventData.position;
		Vector3 nowPos = MgrScene.uiCamera.ScreenToWorldPoint(new Vector3 (pos.x, pos.y, originPos.z));
		transform.position = nowPos;

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
		transform.position = originPos;
	}


	void updateCirclePos(Vector3 delta)
	{
		transform.localPosition = transform.localPosition + delta;
		if (transform.localPosition.magnitude > len) 
		{
			transform.localPosition = transform.localPosition.normalized * len;
		}

		if (onDragListener != null)
			onDragListener (transform.localPosition.normalized);
	}
}
