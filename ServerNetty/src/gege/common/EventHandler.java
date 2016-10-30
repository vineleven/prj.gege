package gege.common;

import gege.common.EventListener.OnEvent;
import gege.consts.EventId;
import gege.util.Logger;

import java.util.HashMap;




public class EventHandler {
	private EventDispatcher m_dispatcher = null;
	private HashMap<EventId, EventListener> m_events = new HashMap<>();
	
	
	public EventHandler() {
	}
	
	
	public EventHandler(EventDispatcher dispatcher) {
		m_dispatcher = dispatcher;
	}
	
	
	
	
	public void addEventCallback(EventId eventId, OnEvent callback){
		if(!m_events.containsKey(eventId)){
			EventListener listener = new EventListener(callback);
			m_events.put(eventId, listener);
		} else {
			Logger.warn("event callback is exsits." + eventId);
		}
	}
	
	
	public void startProcMsg(){
		m_events.forEach((eid, l)->{
			if(m_dispatcher == null)
				EventDispatcher.getGlobalInstance().addListener(eid, l);
			else
				m_dispatcher.addListener(eid, l);
		});
	}
	
	
	public void stopProcMsg(){
		m_events.forEach((eid, l)->{
			l.stop();
		});
		
		// stop之后无法重新start(尚未解决多线程问题)
		m_events.clear();
	}
	
}

