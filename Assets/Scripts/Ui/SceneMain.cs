using UnityEngine;
using System.Collections;
using Global;

public class SceneMain : SceneBase {
	public override void onEnter ()
	{
        Tools.Log("enter SceneMain.");
        
        MgrPanel.openDebugInfo();

        addEventCallback(EventId.MSG_CONNECTED, onConnected);
        addEventCallback(EventId.MSG_GAME_START, onStartGame);
        addEventCallback(EventId.MSG_DISCONNECTED, onDisconnected);
        addEventCallback(EventId.MSG_GAME_OVER, onGameOver);
        startProcMsg();
	}


    void onStartGame(GameEvent e)
    {
        Tools.Log("Game Start");
        MgrPanel.disposeAllPanel(MgrPanel.LAYER_UI);
		MgrPanel.openJoyStick();
    }


    void onGameOver(GameEvent e)
    {
        Tools.Log("Game Over");
        MgrPanel.disposeAllPanel(MgrPanel.LAYER_UI);
        MgrPanel.openMain();
        MgrPanel.openJoyStick();
    }


    void onConnected(GameEvent e)
    {
        MgrPanel.disposeAllPanel(MgrPanel.LAYER_UI);
        MgrPanel.openMain();
        MgrPanel.openJoyStick();
        MgrPanel.openInputName();
    }


    public void onDisconnected(GameEvent e)
    {

    }


    public override void onLeave()
    {
        stopProcMsg();
	}
}
