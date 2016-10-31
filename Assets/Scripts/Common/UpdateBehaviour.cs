using UnityEngine;
using Global;



public class UpdateBehaviour : MonoBehaviour {

    public static UpdateBehaviour Get(GameObject go)
    {
        UpdateBehaviour ft = go.GetComponent<UpdateBehaviour>();
        if (ft == null)
            ft = go.AddComponent<UpdateBehaviour>();

        return ft;
    }



    private Callback m_callback = null;

    public void setUpdateCallback(Callback callback)
    {
        m_callback = callback;
    }


	
	void Update () {
        if (m_callback != null)
            m_callback.Invoke();
	}
}
