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


    public static long getCurTime()
    {
        return DateTime.Now.Ticks / 10000;
    }
}
