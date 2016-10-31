using UnityEngine;
using System.Collections;
using System;

public class Map {



	int[][] m_tileData;
    GameObject m_ground;


    public Map(ArrayList list)
    {
        int row = list.Count;
        //int col;
        m_tileData = new int[row][];
        for (int i = 0; i < row; i++)
        {
            ArrayList cols = list[i] as ArrayList;
            int[] m_cols = new int[cols.Count];
            for (int j = 0; j < cols.Count; j++)
            {
                m_cols[j] = Convert.ToInt32(cols[j]);
            }
            m_tileData[i] = m_cols;
        }
    }



    public Map()
    {
        // random a map
        int row = 30;
        int col = 30;
        m_tileData = new int[row][];
        for (int i = 0; i < row; i++)
        {
            int[] cols = new int[30];
            for (int j = 0; j < col; j++)
            {
                int r = Tools.Random(1, 10);
                r = r > 8 ? 2 : 1;
                cols[j] = r;
            }
            m_tileData[i] = cols;
        }
	}


	public void CreateMap(){
        m_ground = new GameObject();
        m_ground.name = "Ground";

        for (int i = 0; i < m_tileData.Length; i++)
        {
            int[] cols = m_tileData[i];
            for (int j = 0; j < cols.Length; j++)
            {
                //GameObject go = Resources.Load<GameObject> ("Prefab/Tile" + cols[j]);
                GameObject go = MgrRes.newObject("Tile" + cols[j]) as GameObject;
                go.name = "i:" + i + " j:" + j;
                go.transform.SetParent(m_ground.transform);
				go.transform.position = new Vector3 (j, i, 0);
			}
		}
	}


    public Transform getGround()
    {
        return m_ground.transform;
    }


    public void dispose()
    {
        if(m_ground != null)
        {
            GameObject.Destroy(m_ground);
            m_ground = null;
        }
        
    }



    public class MapPath
    {

    }



    public void findPath(int curX, int curY)
    {
        int up = m_tileData
    }

	
}
