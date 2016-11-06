using UnityEngine;
using System.Collections;
using System;



public class Tools {

	public static void Log(string msg)
	{
		Debug.Log (msg);
	}


	public static void LogWarn(string msg)
	{
		Debug.LogWarning (msg);
	}


	public static void LogError(string msg)
	{
		Debug.LogError (msg);
	}


	public static int Random(int min, int max) {
		return UnityEngine.Random.Range(min, max);
	}


    public static int[] RandomArray(int len)
    {
        int[] order = new int[len];
        int rnd;
        for (int i = 1; i <= len; i++)
        {
            do
            {
                rnd = Random(0, len);
            } while (order[rnd] != 0);
            order[rnd] = i;
        }

        return order;
    }


    public static long getCurTime()
    {
        return DateTime.Now.Ticks / 10000;
    }



}
