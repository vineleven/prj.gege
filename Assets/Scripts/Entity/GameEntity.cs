using UnityEngine;
using System.Collections;

public abstract class GameEntity
{
    public GameObject gameObject;
    public Transform transform;


    private bool m_bDisposed = false;


    public GameEntity(string prefabName)
    {
        gameObject = MgrRes.newObject(prefabName) as GameObject;
        transform = gameObject.transform;
    }


    public virtual void dispose(){
        m_bDisposed = true;
        GameObject.Destroy(gameObject);
        gameObject = null;
        transform = null;
    }


    public bool isDisposed()
    {
        return m_bDisposed;
    }


    public virtual void setPosition(float x, float y)
    {
        setPosition(new Vector3(x, y, 0));
    }


    public virtual void setPosition(Vector3 p)
    {
        transform.position = p;
    }


    public Vector3 getPosition()
    {
        return transform.position;
    }


    public bool collisionWith(GameEntity entity)
    {
        //entity.gameObject.transform.
        // 客户端貌似不做判断
        return false;
    }


    public void show()
    {
        if(!m_bDisposed)
            gameObject.SetActive(true);
    }


    public void hide()
    {
        if(!m_bDisposed)
            gameObject.SetActive(false);
    }



    public void update()
    {
        if(!m_bDisposed)
            onUpdate();
    }



    public abstract void onUpdate();
}
