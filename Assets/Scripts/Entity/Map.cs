using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;

public class Map {

    public const int DIR_NONE = -100;
    public const int DIR_UP = 1;
    public const int DIR_RIGHT = 2;
    public const int DIR_DOWN = 3;
    public const int DIR_LEFT = 4;
    

    public const int TILE_NORMAL = 0;
	public const int TILE_PHY = 1;


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
                r = r > 6 ? TILE_PHY : TILE_NORMAL;
                cols[j] = r;
            }
            m_tileData[i] = cols;
        }

        // 避免player堵死
        m_tileData[15][15] = TILE_NORMAL;
        m_tileData[15][16] = TILE_NORMAL;
        m_tileData[15][17] = TILE_NORMAL;
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
                GameObject go = MgrRes.newObject("Tile" + (cols[j] + 1)) as GameObject;
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



    public static int pos2index(float value)
    {
        if (value > 0)
        {
            return (int)(value + 0.5f);
        }
        else
        {
            return (int)(value - 0.5f);
        }
    }


    public static int getDir(Vector3 cur, Vector3 next)
    {
        int iCur = pos2index(cur.y);
        int jCur = pos2index(cur.x);

        int iNext = pos2index(next.y);
        int jNext = pos2index(next.x);

        if (jNext > jCur)
        {
            return DIR_RIGHT;
        }
        else if (jNext < jCur)
        {
            return DIR_LEFT;
        }
        else
        {
            if (iNext > iCur)
            {
                return DIR_UP;
            }
            else if (iNext < iCur)
            {
                return DIR_DOWN;
            }
            else
            {
                return DIR_NONE;
            }

        }
    }


    // 算法有以下假定
    // 1、0.5 为tile的宽度
    // 2、坐标是从0，0点开始的

    // 向指定方向找最多两个点(世界坐标)
    public void findPath(Vector3 curPos, int dir1, int dir2, List<Vector3> list)
    {
        int nextCount = dir2 == DIR_NONE ? 1 : 2;
        int i = pos2index(curPos.y);
        int j = pos2index(curPos.x);

        for (int count = 0; count < nextCount; count++)
        {
            MapPoint p = moveTo(i, j, dir1);
            if (p != null)
            {
                list.Add(point2pos(p));
				i = p.i;
				j = p.j;
            }
            else
            {
                p = moveTo(i, j, dir2);
				if (p != null)
				{
					list.Add(point2pos(p));
					i = p.i;
					j = p.j;
				}
            }
        }
    }


    MapPoint moveTo(int i, int j, int dir)
    {
        switch (dir)
        {
            case DIR_UP:
                return getMapPoint(i + 1, j);
            case DIR_RIGHT:
                return getMapPoint(i, j + 1);
            case DIR_DOWN:
                return getMapPoint(i - 1, j);
            case DIR_LEFT:
                return getMapPoint(i, j - 1);
            default:
                return null;
        }
    }


    MapPoint getMapPoint(int i, int j)
    {
        if (checkMap(i, j))
        {
            return new MapPoint(i, j);
        }
        else
        {
            return null;
        }
    }


    bool checkMap(int i, int j)
    {
        if(i < 0 || i >= m_tileData.Length)
            return false;

        if(j < 0 || j >= m_tileData[i].Length)
            return false;

        return m_tileData[i][j] == TILE_NORMAL;
    }


    Vector3 point2pos(MapPoint p)
    {
        return new Vector3(p.j, p.i, 0);
    }


    public bool checkDir(Vector3 pos, int dir)
    {
        int i = pos2index(pos.y);
        int j = pos2index(pos.x);
        switch (dir)
        {
            case DIR_UP:
                return checkMap(i + 1, j);
            case DIR_RIGHT:
                return checkMap(i, j + 1);
            case DIR_DOWN:
                return checkMap(i - 1, j);
            case DIR_LEFT:
                return checkMap(i, j - 1);
            default:
                return false;
        }
    }









    class MapPoint
    {
        public MapPoint(int i, int j)
        {
            this.i = i;
            this.j = j;
        }
        public int i;
        public int j;
    }
}
