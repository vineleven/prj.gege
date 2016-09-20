using UnityEngine;
using System.Collections;

public class MgrBattle : MonoBehaviour {



	GameObject mainPlayer;

	void Awake(){
		mainPlayer = GameObject.Find ("MainPlayer");
	}


	// Use this for initialization
	void Start () {
		if (mainPlayer != null)
			FollowTarget.Get (MgrScene.battleCamera.gameObject).SetTarget (mainPlayer.transform);

		Map map = new Map ();
		map.CreateMap (MgrScene.ground.transform);
	}
	
	// Update is called once per frame
	void Update () {
	
	}
}
