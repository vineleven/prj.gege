using UnityEngine;
using System.Collections;

public class PlayerDemo : Player
{
    const int CHANGE_DIR_DELTA_TIME = 5000;
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
        int[] dirs = Tools.RandomArray(4);
        for (int i = 0; i < dirs.Length; i++)
        {
            if (MgrBattle.getMap().checkDir(m_nextPosInfo.getNextPos(), dirs[i]))
            {
                m_dir1 = dirs[i];
                break;
            }
        }

        findNextPath();
    }


    public void findNextPathFromUser()
    {
        //m_setDirTime = MgrBattle.getCurTime() + 3000;
        //m_changeDirNextTime = MgrBattle.getCurTime() + CHANGE_DIR_DELTA_TIME;
        //findNextPath();
    }


	public override void onUpdate () {
        base.onUpdate();

        // 玩家在操作
        if (m_setDirTime > MgrBattle.getCurTime())
            return;

        if (m_path.Count < 1)
        {
			findNextPath ();
			if(m_path.Count < 1)
			{
				randomDir();
				m_changeDirNextTime = MgrBattle.getCurTime() + CHANGE_DIR_DELTA_TIME;
			}
        }

        if (m_changeDirNextTime < MgrBattle.getCurTime())
        {
            m_changeDirNextTime = MgrBattle.getCurTime() + CHANGE_DIR_DELTA_TIME;
            randomDir();
        }
	}

}
