using UnityEngine;
using System.Collections;

public class FollowTarget : MonoBehaviour {



	public Transform target;
    public float height = -6f;
    public float distance = -12f;
    
    private float _xRotation = 0;
    public float xRotation = -30;

	public float time = 0.1f;


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
	}


	// Update is called once per frame
	void Update () {
		if (target == null)
            return;

        if (_xRotation != xRotation)
        {
            _xRotation = xRotation;
            transform.rotation = Quaternion.Euler(_xRotation, 0, 0);
        }

		Vector3 myPos = transform.position;
		Vector3 targetPos = target.position;
		float dis = (myPos - targetPos).magnitude;
		Vector3 movement = new Vector3 (0, height, distance);
		Vector3 destPos = targetPos + movement;

		if (dis > 10) {
			transform.position = destPos;
		} else {
            transform.position = Vector3.Lerp(myPos, destPos, time);
		}
	}
}
