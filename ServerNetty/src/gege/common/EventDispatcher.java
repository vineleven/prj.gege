package gege.common;

import gege.consts.EventId;
import gege.util.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;



/**
 * 
 * 非线程安全
 * 
 * @author vineleven
 */

public class EventDispatcher {
	
	private static EventDispatcher inst = new EventDispatcher();
	public synchronized static EventDispatcher getGlobalInstance(){
		return inst;
	}
	
	
	
	private HashMap<EventId, LinkedList<EventListener>> allListeners = new HashMap<>();
	
	
	public EventDispatcher() {
	}
	
	
	public void addListener( EventId event, EventListener listener ){
		if( !checkListener(event, listener) ){
			allListeners.get(event).add( listener );
		}
	}
	
	
	private boolean checkListener( EventId eventId, EventListener listener ){
		LinkedList<EventListener> listeners =  allListeners.get(eventId);
		if(listeners == null){
			allListeners.put(eventId, new LinkedList<EventListener>());
		} else {
			for (Iterator<EventListener> iterator = listeners.iterator(); iterator.hasNext();) {
				EventListener l = iterator.next();
				if(l == listener){
					Logger.error( "Alread has EventListener in EventId:[" + eventId + "]." + " listener:[" + listener.toString() + "]." );
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	/**
	 * 先添加的会先收到消息
	 */
	public void dispatchEvent(EventId eventId, Object data){
		LinkedList<EventListener> listeners =  allListeners.get(eventId);
		if(listeners != null){
			GameEvent e = new GameEvent(data);
			for (Iterator<EventListener> iterator = listeners.iterator(); iterator.hasNext();) {
				EventListener listener = iterator.next();
				if(listener.bStop()){
					iterator.remove();
					continue;
				}
				
				listener.onEvent(e);
				if(e.bStop())
					break;
			}
		}
	}
	
	
	public void removeAllUnused(){
		allListeners.forEach((eid, list) ->{
			for (Iterator<EventListener> iterator = list.iterator(); iterator.hasNext();) {
				EventListener listener = iterator.next();
				if(listener.bStop())
					iterator.remove();
			}
		});
	}
	
}

