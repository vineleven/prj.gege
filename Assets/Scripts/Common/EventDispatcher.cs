using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class EventDispatcher {

	private static EventDispatcher inst = new EventDispatcher();
	public static EventDispatcher getGlobalInstance(){
		return inst;
	}



	private Dictionary<Event, List<EventListener>> allListeners = new Dictionary<Event, List<EventListener>>();



	public EventDispatcher() {
	}


	public void addListener( Event e, EventListener listener ){
		if( !checkListener(e, listener) ){
			allListeners [e].Add (listener);
		}else {
			Tools.LogError( "Alread has EventListener in Event:[" + e + "]." + " listener:[" + listener.ToString() + "]." );
		}
	}


	private bool checkListener( Event e, EventListener listener ){
		if(allListeners.ContainsKey(e)){
			var list = allListeners [e];
			list.Remove (listener);
			return list.Contains (listener);
		} else {
			allListeners.Add(e, new List<EventListener>());
		}

		return false;
	}


	/**
	 * 先添加的会先收到消息
	 */
	public void dispatchEvent( Event e, Object data ){
		List<EventListener> listeners =  allListeners[e];
		foreach(var listener in listeners){
			if(listener.onEvent(data)){
				break;
			}
		}
	}


	public void removeListener( EventListener listener ){
		foreach(var e in allListeners.Keys){
			removeListener (e, listener);
		}
	}


	public void removeListener(Event e, EventListener listener){
		allListeners [e].Remove (listener);
	}


}