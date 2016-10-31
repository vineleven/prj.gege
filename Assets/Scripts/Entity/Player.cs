using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class Player : GameEntity
{

    float m_speed;

    int m_group;
    int m_index;


    protected List<Vector3> m_path = new List<Vector3>(2);

    // 记录下一个点
    PosInfo m_nextPosInfo;

    public Player(string prefab, int group, int idx, float x, float y, float speed)
        : base(prefab)
    {
        m_group = group;
        m_index = idx;
        m_speed = speed;

        Vector3 pos = new Vector3(x, y, 0);
        m_nextPosInfo = new PosInfo(pos, pos, 1, 0);
        setPosition(pos);
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


    public void findNextPath(int dir1, int dir2)
    {
        m_path.Clear();
        MgrBattle.getMap().findPath(m_nextPosInfo.getDestPos(), dir1, dir2, m_path);
    }


    public void notifyServerPath()
    {

    }


    public override void onUpdate()
    {
        Vector3 curPos = getPosition();
        if (!m_nextPosInfo.isArrived(curPos))
        {
            //Vector3 next = m_nextPosInfo.Lerp(curPos, MgrBattle.curTime);
            //Tools.Log("move len:" + (curPos - next).magnitude + " time:" + Tools.getCurTime() + " delta:" + MgrBattle.deltaTime);
            setPosition(m_nextPosInfo.Lerp(MgrBattle.curTime));
        }
        else if (m_path.Count > 0)
        {
            Vector3 nextPos = m_path[0];
            if (!m_nextPosInfo.isArrived(nextPos))
            {
                int time = (int)((nextPos - curPos).magnitude / m_speed);
                //Tools.Log("move time:" + time + " moveLen:" + (nextPos - curPos).magnitude + " speed:" + m_speed + " deltaTime:" + (MgrBattle.curTime - ____timeNow) + " realTime:" + (Time.time - _____timeNow2));
                m_nextPosInfo = new PosInfo(curPos, nextPos, time, MgrBattle.curTime + time);
                m_path.RemoveAt(0);
            }
            else
            {
                m_path.RemoveAt(0);
                // 到这里是目测是因为找到了一个跟当前坐标相同的点
                Tools.LogWarn("----> no no no no. can't be here.");
            }
        }

	}



    public class PosInfo
    {
        Vector3 m_originPos;
        Vector3 m_nextPos;
        long m_arriveTime;
        float m_delta;


        public PosInfo(Vector3 originPos, Vector3 nextPos, int delta, long arriveTime)
        {
            m_originPos = originPos;
            m_nextPos = nextPos;
            m_delta = delta;
            m_arriveTime = arriveTime;
        }


        public Vector3 getDestPos()
        {
            return m_nextPos;
        }


        public bool isArrived(Vector3 descPos)
        {
            if (Mathf.Abs(m_nextPos.x - descPos.x) < 0.0001f)
                if(Mathf.Abs(m_nextPos.y - descPos.y) < 0.0001f)
                    return true;

            return false;
        }


        public Vector3 Lerp(long curTime)
        {
            float f = (m_arriveTime - curTime) / m_delta;
            if (f <= 0)
                f = 0f;
            else if (f >= 1)
                f = 1f;
            return Vector3.Lerp(m_originPos, m_nextPos, 1 - f);
        }
    }
}
