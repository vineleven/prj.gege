using UnityEngine;
using System.Collections;

public class Player : GameEntity
{

    public float m_speed;

    int m_group;
    int m_index;

    float m_nextX;
    float m_nextY;

    public Player(string prefab, int group, int idx)
        : base(prefab)
    {
        m_group = group;
        m_index = idx;
    }


    public int getGroup()
    {
        return m_group;
    }


    public int getIndex()
    {
        return m_index;
    }


    public void setSpeed(float speed)
    {
        m_speed = speed;
    }


    public void setNextPosition()
    {

    }

	
	public override void update () {
	
	}
}
