package gege.common;

import gege.consts.Event;
import gege.impl.EventListener;
import gege.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * 
 * 非线程安全
 * 
 * @author vineleven
 */

public class EventDispatcher {
	
	private static EventDispatcher inst = new EventDispatcher();
	public static EventDispatcher getGlobalInstance(){
		return inst;
	}
	
	
	
	private HashMap<Event, ArrayList<EventListener>> allListeners = new HashMap<>();
	
	
	public EventDispatcher() {
	}
	
	
	public void addListener( Event event, EventListener listener ){
		if( !checkListener(event, listener) ){
			allListeners.get(event).add( listener );
		}
	}
	
	
	private boolean checkListener( Event event, EventListener listener ){
		ArrayList<EventListener> listeners =  allListeners.get(event);
		if(listeners == null){
			allListeners.put(event, new ArrayList<EventListener>());
		} else {
			// TODO: 可以考虑去掉验证
			for (int i = 0; i < listeners.size(); i++) {
				if( listeners.get( i ) == listener ){
					Logger.error( "Alread has EventListener in Event:[" + event + "]." + " listener:[" + listener.toString() + "]." );
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	/**
	 * 先添加的会先收到消息
	 */
	public void dispatchEvent( Event event, Object data ){
		ArrayList<EventListener> listeners =  allListeners.get(event);
		int size = listeners.size();
		for (int i = 0; i < size; i++) {
			if( listeners.get(i).onEvent(data) )
				break;
		}
	}
	
	
	public void removeListener( EventListener listener ){
		for (Event e : Event.values()) {
			removeListener(e, listener);
		}
	}
	
	
	public void removeListener(Event e, EventListener listener){
		allListeners.get(e).remove(listener);
	}
	
	
}

