using UnityEngine;
using System.Collections;

public class SceneMain : SceneBase {
	public override void onEnter ()
	{
        Tools.Log("enter SceneMain.");

        MgrPanel.openMain();
        MgrPanel.openDebugInfo();
        MgrPanel.openInputName();
	}


	public override void onLevel(){
		
	}
}
