package gege.common;

import gege.consts.Event;
import gege.impl.EventListener;
import gege.util.Logger;

import java.util.ArrayList;




public class EventDispatcher {
	private ArrayList<ArrayList<EventListener>> allListeners;
	
	
	
	public EventDispatcher() {
		int size = Event.getCount();
		allListeners = new ArrayList<ArrayList<EventListener>>( size );
		for (int i = 0; i < size; i++) {
			allListeners.add( new ArrayList<EventListener>() );
		}
	}
	
	
	public void addListener( Event event, EventListener listener ){
		if( !hasListener( event, listener ) ){
			allListeners.get( event.getId() ).add( listener );
		}
	}
	
	
	private boolean hasListener( Event event, EventListener listener ){
		// TODO: 可以考虑去掉验证
		ArrayList<EventListener> listeners =  allListeners.get( event.getId() );
		for (int i = 0; i < listeners.size(); i++) {
			if( listeners.get( i ) == listener ){
				Logger.error( "Alread has EventListener in Event:[" + event + "]." + " listener:[" + listener.toString() + "]." );
				return true;
			}
		}
		
		return false;
	}
	
	
	public void dispatchEvent( Event event, Object ... datas ){
		ArrayList<EventListener> listeners =  allListeners.get( event.getId() );
		int size = listeners.size();
		for (int i = 0; i < size; i++) {
			if( listeners.get( i ).onEvent( event, datas ) )
				break;
		}
	}
	
	
	public void removelistener( EventListener listener ){
		
	}
	
	
}

