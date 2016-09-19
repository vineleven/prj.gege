using UnityEngine;
using System.Collections;

public class MgrScene : MonoBehaviour {

	// Use this for initialization


	public static Camera uiCamera;



	void Awake(){
		uiCamera = GameObject.FindWithTag ("UiCamera").GetComponent<Camera>();
	}


	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
