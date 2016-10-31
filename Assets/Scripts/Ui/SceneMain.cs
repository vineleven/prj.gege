using UnityEngine;
using System.Collections;
using Global;

public class SceneMain : SceneBase {
	public override void onEnter ()
	{
        Tools.Log("enter SceneMain.");

        MgrPanel.openMain();
        MgrPanel.openDebugInfo();
        MgrPanel.openInputName();


        addEventCallback(EventId.MSG_START_GAME, onStartGame);
        startProcMsg();
	}


    public void onStartGame(GameEvent e)
    {
        Tools.Log("start Game");
        MgrPanel.disposeAllPanel(MgrPanel.LAYER_UI);
        MgrPanel.openJoyStick();


    }


    public override void onLeave()
    {
        stopProcMsg();
	}
}
