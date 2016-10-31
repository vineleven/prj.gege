using UnityEngine;
using System.Collections;

public class PlayerDemo : Player
{


    const int CHANGE_DIR_DELTA_TIME = 4000;
    const float SPEED = 2f / 1000;


    int m_dir1;
    int m_dir2;


    long m_changeDirNextTime = 0;

    public PlayerDemo(string prefab)
        : base(prefab, 0, 0, 15, 15, SPEED)
    {
        randomDir();
    }



    void randomDir()
    {
        m_dir1 = Tools.Random(1, 5);
        do
        {
            m_dir2 = Tools.Random(1, 5);
        } while (m_dir1 == m_dir2 || Mathf.Abs(m_dir1 - m_dir2) == 2);

        //Tools.Log("rnd dir:" + m_dir1 + " " + m_dir2);
    }



	public override void onUpdate () {
        base.onUpdate();
        if (m_path.Count < 1)
        {
            findNextPath(m_dir1, m_dir2);
        }

        if (m_changeDirNextTime < MgrBattle.curTime)
        {

            m_changeDirNextTime = MgrBattle.curTime + CHANGE_DIR_DELTA_TIME;
            randomDir();
        }
	}
}
