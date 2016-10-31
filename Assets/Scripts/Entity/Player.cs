using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class Player : GameEntity
{

    float m_speed;

    int m_group;
    int m_index;

    protected int m_dir1;
    protected int m_dir2;

    protected List<Vector3> m_path = new List<Vector3>(2);

    // 记录下一个点
    protected PosInfo m_nextPosInfo;

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


    public void setDir(Vector3 offset)
    {
        int dir1, dir2;
        if (offset.y > 0)
            dir1 = Map.DIR_UP;
        else
            dir1 = Map.DIR_DOWN;

        if (offset.x > 0)
            dir2 = Map.DIR_RIGHT;
        else
            dir2 = Map.DIR_LEFT;

        if (Mathf.Abs(offset.x) > Mathf.Abs(offset.y))
        {
            int temp = dir1;
            dir1 = dir2;
            dir2 = temp;
        }

        setDir(dir1, dir2);
    }


    public void setDir(int dir1, int dir2)
    {
        m_dir1 = dir1;
        m_dir2 = dir2;
        //Tools.Log("rnd dir:" + m_dir1 + " " + m_dir2);
    }


    public void findNextPath()
    {
        m_path.Clear();
        Vector3 curPos = getPosition();
        MgrBattle.getMap().findPath(curPos, m_dir1, m_dir2, m_path);
        if (m_path.Count > 0)
        {
            if (!m_nextPosInfo.getNextPos().Equals(m_path[0]))
            {
                int nextDir = Map.getDir(curPos, m_path[0]);
                if (m_nextPosInfo.isNegative(nextDir))
                {
                    // 如果下个目标点有变化切是反方向，则立即改变
                    gotoNext(curPos);
                }
            }
        }
    }


    public void notifyServerPath()
    {

    }


    public override void onUpdate()
    {
        updatePos();
	}


    void updatePos()
    {
        Vector3 curPos = getPosition();
        tryMoveWithCurPos(curPos);
    }


    void tryMoveWithCurPos(Vector3 curPos)
    {
        Vector3 nextPos = m_nextPosInfo.Lerp(MgrBattle.curTime);

        if (m_nextPosInfo.isArrived(curPos))
        {
            gotoNext(m_nextPosInfo.getNextPos());
        }
        else
        {
            // 预测下个点
            Vector3 nextNextPos = m_nextPosInfo.Lerp(MgrBattle.curTime + MgrBattle.deltaTime);
            if (m_nextPosInfo.isArrived(nextNextPos))
            {
                gotoNext(nextPos);
            }
        }

        setPosition(nextPos);
    }


    void gotoNext(Vector3 from)
    {
        if (m_path.Count > 0)
        {
            Vector3 nextPos = m_path[0];

            int time = (int)((nextPos - from).magnitude / m_speed);
            m_nextPosInfo = new PosInfo(from, nextPos, time, MgrBattle.curTime + time);

            m_path.RemoveAt(0);
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


        public int getDir()
        {
            return Map.getDir(m_originPos, m_nextPos);
        }


        public bool isNegative(int dir)
        {
            return Mathf.Abs(getDir() - dir) == 2;
        }


        public Vector3 getNextPos()
        {
            return m_nextPos;
        }


        public void setNext(Vector3 destPos)
        {
            m_nextPos = destPos;
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
