using System;
using System.Collections;
using System.Collections.Generic;




namespace Global
{
    public class GameEvent
    {

        private Object m_data = null;
        private bool m_stop = false;

        public GameEvent()
        {
        }

        public GameEvent(Object data)
        {
            m_data = data;
        }


        public void stop()
        {
            m_stop = true;
        }


        public bool bStop()
        {
            return m_stop;
        }


        public Object getData()
        {
            return m_data;
        }

    }


    public delegate void OnEvent(GameEvent e);


    public class EventListener
    {
        private OnEvent m_callback;

        private bool m_stop = false;
        private bool m_pause = false;

        public EventListener(OnEvent callback)
        {
            m_callback = callback;
        }


        public void onEvent(GameEvent e)
        {
            m_callback(e);
        }


        public void stop()
        {
            m_stop = true;
            m_callback = null;
        }


        public bool bStop()
        {
            return m_stop;
        }


        public void pause()
        {
            m_pause = true;
        }


        public void resume()
        {
            m_pause = false;
        }


        public bool bPause()
        {
            return m_pause;
        }
    }



    public class EventHandler {

        private Dictionary<EventId, EventListener> m_events = new Dictionary<EventId, EventListener>();

        public void addEventCallback(EventId eventId, OnEvent callback)
        {
		    if(!m_events.ContainsKey(eventId)){
			    EventListener listener = new EventListener(callback);
                m_events.Add(eventId, listener);
		    } else {
			    Tools.LogWarn("event callback is exsits." + eventId);
		    }
	    }
	
	
	    public void startProcMsg(){
            //m_events.forEach((eid, l)->{
            //    EventDispatcher.getGlobalInstance().addListener(eid, l);
            //});
            foreach (var pair in m_events)
            {
                EventDispatcher.getGlobalInstance().addListener(pair.Key, pair.Value);
            }
	    }
	
	
	    public void stopProcMsg(){
            foreach (var l in m_events.Values)
            {
                l.stop();
            }
		
		    // stop之后无法重新start(尚未解决多线程问题)
            m_events.Clear();
	    }
	
    }


    public delegate void Callback();
    public delegate void CallbackWithParam(object obj);
}

