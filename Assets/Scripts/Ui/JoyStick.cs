using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;


public class JoyStick : MonoBehaviour {

	// Use this for initialization

	Vector3 originPos;

	float len = 50;

	void Start () {
		originPos = transform.localPosition;

		UIEventListener.Get (gameObject).onDown = OnDown;
		UIEventListener.Get (gameObject).onDrag = OnDrag;
		UIEventListener.Get (gameObject).onUp = OnUp;
		Tools.Log ("origin:" + originPos.x + "  " + originPos.y);
	}


	void OnDown(GameObject go, PointerEventData eventData)
	{
		Vector2 pos = eventData.position;
		Vector3 nowPos = new Vector3 (pos.x, pos.y);
		transform.position = nowPos;
		transform.localPosition = transform.localPosition.normalized * len;
	}


	void OnDrag(GameObject go, PointerEventData eventData)
	{
		Vector2 pos = eventData.delta;
		Vector3 nowPos = new Vector3 (pos.x, pos.y);
		transform.localPosition = transform.localPosition + nowPos;
		if (transform.localPosition.magnitude > len) {
			transform.localPosition = transform.localPosition.normalized * len;
		}

		Tools.Log ("origin:" + transform.localPosition.x + "  " + transform.localPosition.y);
	}


	void OnUp(GameObject go, PointerEventData eventData)
	{
//		Tools.Log ("origin:" + originPos.x + "  " + originPos.y);
		transform.localPosition = originPos;
	}
}
