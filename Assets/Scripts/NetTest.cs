using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class NetTest : MonoBehaviour {

	// Use this for initialization
	void Start () {
		var com = GetComponent<Button>();
        com.onClick.AddListener(OnClickButton);
	}


    void OnClickButton()
    {
        Hashtable data = new Hashtable();
        data.Add("cRnd", Tools.Random(1000, 10000));

        MgrNet.Send(Cmd.C2S_GET_TIME, data);
    }
	
	// Update is called once per frame
	void Update () {
	
	}
}
