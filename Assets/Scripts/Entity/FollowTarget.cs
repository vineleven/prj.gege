using UnityEngine;
using System.Collections;

public class FollowTarget : MonoBehaviour {



	Transform target;
	float height = -5.7f;
	float distance = -4.2f;
	float xRotation = -60;
	public float damping = 3;


	public static FollowTarget Get(GameObject go)
	{
		FollowTarget ft = go.GetComponent<FollowTarget> ();
		if (ft == null)
			ft = go.AddComponent<FollowTarget> ();

		return ft;
	}


	public void SetTarget(Transform tf)
	{
		target = tf;
	}


	// Use this for initialization
	void Start () {
		transform.rotation = Quaternion.Euler (xRotation, 0, 0);
	}


	// Update is called once per frame
	void Update () {
		if (target == null)
			return;

		Vector3 myPos = transform.position;
		Vector3 targetPos = target.position;
		float dis = (myPos - targetPos).magnitude;
		Vector3 movement = new Vector3 (0, height, distance);
		Vector3 destPos = targetPos + movement;

		if (dis > 10) {
			transform.position = destPos;
		} else {
            transform.position = Vector3.Lerp(myPos, destPos, Time.deltaTime * damping);
		}
	}
}
