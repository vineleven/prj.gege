package gege.game;

import java.util.List;

import gege.util.Logger;
import gege.util.Mathf;

import org.json.JSONArray;

public class GameMap {
    public final static int DIR_NONE = -100;
    public final static int DIR_UP = 1;
    public final static int DIR_RIGHT = 2;
    public final static int DIR_DOWN = 3;
    public final static int DIR_LEFT = 4;
    
    
	final public static int TILE_NORMAL = 0;
	final public static int TILE_PHY = 1;
	public int[][] m_tileData;
	private int m_row;
	private int m_col;
	
	
	public GameMap() {
		m_row = template.length;
		m_col = template[0].length;
		m_tileData = template;
	}
	
	
	GameMap(int row, int col, int rate){
		m_row = row;
		m_col = col;
		m_tileData = new int[row][col];
		
		// 随机一个地图
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				int r = Mathf.randomInt(0, 10);
				r = r < rate ? TILE_PHY : TILE_NORMAL;
				m_tileData[i][j] = r;
			}
		}
		
//		tileData = new int[][]{
//				{1,2},
//				{2,1}
//		};
	}
	
	
	public Vector3 getEmptyPos(){
		int rnd = getEmptyPosByInt();
		if(rnd != -1)
			return getVector3ByInt(rnd);
		
		return null;
	}
	
	
	public int getEmptyPosByInt(){
		int rnd, x, y;
		int total = m_row * m_col;
		//1000 次保护
		for (int i = 0; i < 1000 ;) {
			rnd = Mathf.randomInt(0, total);
			
			y = rnd % m_row;
			x = rnd / m_row;
			
			if(m_tileData[y][x] == TILE_NORMAL){
				return rnd;
			}
			i++;
		}
		
		Logger.error("can't find rnd pos by int");
		return -1;
	}
	
	
	public Vector3 getVector3ByInt(int i){
		return new Vector3(i / m_row, i % m_row);
	}
	
	
	public JSONArray toJSONArray(){
		JSONArray arr = new JSONArray();
		for (int i = 0; i < m_tileData.length; i++) {
			JSONArray arr1 = new JSONArray();
			for (int j = 0; j < m_tileData[i].length; j++) {
				arr1.put(m_tileData[i][j]);
			}
			arr.put(arr1);
		}
		
		return arr;
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
	
	
    public void findPath(Vector3 curPos, int dir1, int dir2, List<Vector3> list)
    {
        int nextCount = dir2 == DIR_NONE ? 1 : 2;
        int i = pos2index(curPos.y);
        int j = pos2index(curPos.x);

        for (int count = 0; count < nextCount; count++)
        {
            Vector3 p = moveTo(i, j, dir1);
            if (p != null)
            {
                list.add(p);
				i = (int) p.y;
				j = (int) p.x;
            }
            else
            {
                p = moveTo(i, j, dir2);
				if (p != null)
				{
					list.add(p);
					i = (int) p.y;
					j = (int) p.x;
				}
            }
        }
    }
    
    
    Vector3 moveTo(int i, int j, int dir)
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
    
    
    Vector3 getMapPoint(int i, int j)
    {
        if (checkMap(i, j))
        {
            return new Vector3(j, i);
        }
        else
        {
            return null;
        }
    }
    
    
    boolean checkMap(int i, int j)
    {
        if(i < 0 || i >= m_tileData.length)
            return false;

        if(j < 0 || j >= m_tileData[i].length)
            return false;

        return m_tileData[i][j] == TILE_NORMAL;
    }
    
    
    final static int[][] template = {
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
		{1,0,1,1,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,1,1,0,1},
		{1,0,1,1,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,1,1,0,1},
		{1,0,0,0,0,0,0,1,1,0,0,0,0,1,1,0,0,0,0,1,1,0,0,0,0,0,0,1},
		{1,1,1,0,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,0,1,1,1},
		{1,1,1,0,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,0,1,1,1},
		{1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1},
		{1,0,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
		{1,0,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
		{1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
		{1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1},
		{1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1},
		{1,1,1,1,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,1,1,1,1},
		{1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1},
		{1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1},
		{0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0},
		{1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1},
		{1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,1,1},
		{1,1,1,1,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,1,1,1,1},
		{1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,1,1},
		{1,1,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,1,1},
		{1,0,0,0,0,0,0,1,1,0,0,0,0,1,1,0,0,0,0,1,1,0,0,0,0,0,0,1},
		{1,0,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,0,1},
		{1,0,1,1,1,1,0,1,1,0,1,1,1,1,1,1,1,1,0,1,1,0,1,1,1,1,0,1},
		{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
		{1,0,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
		{1,0,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
		{1,0,1,1,1,1,0,1,1,1,1,1,0,1,1,0,1,1,1,1,1,0,1,1,1,1,0,1},
		{1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    };
}
