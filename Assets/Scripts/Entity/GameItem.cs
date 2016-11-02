using UnityEngine;
using System.Collections;

public class GameItem : GameEntity {


    public const int TYPE_RED = 0;
    public const int TYPE_BLUE = 1;


    int m_type = -1;
    int m_id;

    public GameItem(string prefab, int type, int id, float x, float y)
        : base(prefab)
    {
        m_type = type;
        m_id = id;
        setPosition(x, y);
    }


    public int getId()
    {
        return m_id;
    }


    public override void onUpdate()
    {
    }
}
