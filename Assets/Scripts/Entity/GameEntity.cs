using UnityEngine;
using System.Collections;

public class GameEntity
{
    public GameObject gameObject;
    public Transform transform;


    public float m_x;
    public float m_y;


    public GameEntity(string prefabName)
    {
        gameObject = MgrRes.newObject(prefabName) as GameObject;
        transform = gameObject.transform;
    }


    public virtual void dispose(){
        GameObject.Destroy(gameObject);
        gameObject = null;
        transform = null;
    }


    public void setPosition(float x, float y)
    {
        m_x = x;
        m_y = y;
        transform.position = new Vector3(x, y, 0);
    }


    public float getX()
    {
        return m_x;
    }


    public float getY()
    {
        return m_y;
    }


    public bool collisionWith(GameEntity entity)
    {
        //entity.gameObject.transform.
        // 客户端貌似不做判断
        return false;
    }


	
	public virtual void update () {
	}
}
