using UnityEngine;
using System.Collections;

public class PlayerDemo : Player
{
    const int CHANGE_DIR_DELTA_TIME = 3000;
    const float SPEED = 3f / 1000;


    long m_changeDirNextTime = 0;

    long m_setDirTime = 0;


    public PlayerDemo(string prefab)
        : base(prefab, 0, 0, 15, 15, SPEED)
    {
        randomDir();
    }



    void randomDir()
    {
        Vector3 dir = Vector3.zero;
        dir.x = Tools.Random(-10, 11);
        dir.y = Tools.Random(-10, 11);
        setDir(dir);

        int count = 0;
        while (count++ < 60 && m_path.Count < 1)
        {
            findNextPath(false);
        }
    }


    public void findNextPathFromUser()
    {
        m_setDirTime = MgrBattle.getCurTime() + 3000;
        m_changeDirNextTime = MgrBattle.getCurTime() + CHANGE_DIR_DELTA_TIME;
        findNextPath(true);
    }


	public override void onUpdate () {
        base.onUpdate();

        // 玩家在操作
        if (m_setDirTime > MgrBattle.getCurTime())
            return;

        if (m_path.Count < 1)
        {
            int count = 0;
            while (count++ < 10 && m_path.Count < 1)
            {
                findNextPath(false);
            }
        }

        if (m_changeDirNextTime < MgrBattle.getCurTime())
        {
            m_changeDirNextTime = MgrBattle.getCurTime() + CHANGE_DIR_DELTA_TIME;
            randomDir();
        }
	}

}
