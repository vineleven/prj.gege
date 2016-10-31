using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;
using Global;

public class MgrTimer : MgrBase
{


    class TimerEvent
    {
        public long endTime = 0;
        public CallbackWithParam callback = null;
        public object param = null;

        public TimerEvent(long endTime, CallbackWithParam callback, object param = null)
        {
            this.endTime = endTime;
            this.callback = callback;
            this.param = param;
        }
    }


    static LinkedList<TimerEvent> list = new LinkedList<TimerEvent>();



	void Start () {
	}


    public override void onDestory()
    {
        list.Clear();
    }


	
	void Update()
    {
        try
        {
            EventDispatcher.getGlobalInstance().procUiEvent();

            var curTime = Tools.getCurTime();
            var node = list.First;

            while (node != null)
            {
                var e = node.Value;
                var next = node.Next;
                if (e.endTime <= curTime)
                {
                    list.Remove(node);
                    e.callback(e.param);
                }

                node = next;
            }
        }
        catch (Exception e)
        {
            Tools.LogError("MgrTimer:" + e.Message);
            Tools.LogError(e.StackTrace);
        }
        

	}


    /**
     * time 毫秒
     */
    public static void callLaterTime(int time, CallbackWithParam callback, object param = null)
    {
        list.AddLast(new TimerEvent(time + Tools.getCurTime(), callback, param));
    }
}

