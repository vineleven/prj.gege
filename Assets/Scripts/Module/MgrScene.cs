using UnityEngine;
using System.Collections;

public class MgrScene : MonoBehaviour {

	// Use this for initialization


	public static Camera uiCamera;
	public static Camera battleCamera;
	public static GameObject ground;


	void Awake(){
		uiCamera = GameObject.Find ("UIRoot/UiCamera").GetComponent<Camera>();
		battleCamera = GameObject.Find("BattleCamera").GetComponent<Camera>();
		ground = GameObject.Find ("Ground");
//		battleCamera.enabled = false;
	}


	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
