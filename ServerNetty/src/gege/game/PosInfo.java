package gege.game;

import gege.util.Mathf;



public class PosInfo
{
	float m_originX;
	float m_originY;
	
	float m_nextX;
	float m_nextY;
	
    long m_arriveTime;
    float m_delta;


    public PosInfo(float originX, float originY, float nextX, float nextY, int delta, long arriveTime)
    {
        reset(originX, originY, nextX, nextY, delta, arriveTime);
    }


    public void reset(float originX, float originY, float nextX, float nextY, int delta, long arriveTime)
    {
    	m_originX = originX;
    	m_originY = originY;
    	m_nextX = nextX;
    	m_nextY = nextY;
        m_delta = delta;
        m_arriveTime = arriveTime;
    }
    

    public boolean isArrived(float nextX, float nextY)
    {
        if (Math.abs(m_nextX - nextX) < 0.0001f)
            if(Math.abs(m_nextY - nextY) < 0.0001f)
                return true;

        return false;
    }

//    public JSONObject ToData()
//    {
//    	JSONObject data = new JSONObject();
//    	data.put("t", m_arriveTime);
//    	data.put("x", m_nextX);
//    	data.put("y", m_nextY);
//        return data;
//    }
    
    
    
    public float lerpX(float t){
    	return Mathf.lerp(m_originX, m_nextX, t);
    }
    
    
    public float lerpY(float t){
    	return Mathf.lerp(m_originX, m_nextX, t);
    }
}
