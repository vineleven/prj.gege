﻿using UnityEngine;
using System.Collections;

public class PlayerDemo : Player
{
    const int CHANGE_DIR_DELTA_TIME = 6000;
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
        int dir1, dir2;
        dir1 = Tools.Random(1, 5);
        do
        {
            dir2 = Tools.Random(1, 5);
        } while (dir1 == dir2 || Mathf.Abs(dir1 - dir2) == 2);

        setDir(dir1, dir2);
    }


    public void findNextPathAuto()
    {
        m_setDirTime = MgrBattle.getCurTime() + 3000;
        m_changeDirNextTime = MgrBattle.getCurTime() + CHANGE_DIR_DELTA_TIME;

        findNextPath();
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
                findNextPath();
            }
        }

        if (m_changeDirNextTime < MgrBattle.getCurTime())
        {
            m_changeDirNextTime = MgrBattle.getCurTime() + CHANGE_DIR_DELTA_TIME;
            randomDir();
        }
	}
}