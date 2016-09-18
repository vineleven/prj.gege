using UnityEngine;
using System.Collections;

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
}
