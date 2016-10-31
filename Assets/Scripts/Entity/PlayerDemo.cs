using UnityEngine;
using System.Collections;

public class PlayerDemo : Player
{

    public PlayerDemo(string prefab)
        : base(prefab, 0, 0)
    {
        setSpeed(0.5f);
    }


	public override void update () {
	    
	}
}
