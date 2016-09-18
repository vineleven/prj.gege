using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;


public class JoyStick : MonoBehaviour {

	// Use this for initialization

	Transform circle;


	Vector3 originPos;

	void Start () {
		circle = transform.FindChild ("Circle");
		originPos = circle.localPosition;

		UIEventListener.Get (circle.gameObject).OnPointerDown = onDown;
		UIEventListener.Get (circle.gameObject).onDrag = onDrag;
		UIEventListener.Get (circle.gameObject).OnPointerDown = onUp;
	}


	void onDown(GameObject go, PointerEventData eventData)
	{
		
	}


	void onDrag(GameObject go, PointerEventData eventData)
	{

	}


	void onUp(GameObject go, PointerEventData eventData)
	{

	}
}
