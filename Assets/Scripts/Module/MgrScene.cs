using UnityEngine;
using System.Collections;

public class MgrScene : MonoBehaviour {

	// Use this for initialization


	public static Camera uiCamera;
	public static Camera battleCamera;
	public static GameObject ground;


	void Awake()
    {
		uiCamera = GameObject.Find ("UIRoot/UiCamera").GetComponent<Camera>();
		battleCamera = GameObject.Find("BattleCamera").GetComponent<Camera>();
		ground = GameObject.Find ("Ground");
//		battleCamera.enabled = false;
	}


	static SceneBase m_curScene;
	void Start ()
    {
        openNextScene(new SceneMain());
	}


    void OnDestroy()
    {
        if (m_curScene != null)
            m_curScene.onLevel();

        m_curScene = null;
    }


    public static void openNextScene(SceneBase scene)
    {
        if (m_curScene != null)
            m_curScene.onLevel();

        MgrPanel.disposeAllPanel();

        m_curScene = scene;
        m_curScene.onEnter();
    }
	

	// Update is called once per frame
	void Update ()
    {
	}
}
