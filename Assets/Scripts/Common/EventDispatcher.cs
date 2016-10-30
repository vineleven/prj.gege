using System.Collections;
using System.Collections.Generic;
using Global;

public class EventDispatcher {

	private static EventDispatcher inst = new EventDispatcher();
	public static EventDispatcher getGlobalInstance(){
		return inst;
	}



    private Dictionary<EventId, LinkedList<EventListener>> allListeners = new Dictionary<EventId, LinkedList<EventListener>>();



	public EventDispatcher() {
	}


    public void addListener(EventId eid, EventListener listener)
    {
		if( !checkListener(eid, listener) ){
            allListeners[eid].AddLast(listener);
		}else {
			Tools.LogError( "Alread has EventListener in Event:[" + eid + "]." + " listener:[" + listener.ToString() + "]." );
		}
	}


    private bool checkListener(EventId eid, EventListener listener)
    {
		if(allListeners.ContainsKey(eid)){
			var list = allListeners [eid];
			return list.Contains (listener);
		} else {
            allListeners.Add(eid, new LinkedList<EventListener>());
		}

		return false;
	}


	/**
	 * 先添加的会先收到消息
	 */
	public void dispatchEvent(EventId eid, object data){
        if (!allListeners.ContainsKey(eid))
        {
            Tools.LogWarn("can't find event:" + eid.ToString());
            return;
        }
        var listeners = allListeners[eid];

        var e = new GameEvent(data);

        var node = listeners.First;
        while (node != null)
        {
            var listener = node.Value;
            if (listener.bStop())
            {
                var next = node.Next;
                listeners.Remove(node);
                node = next;
                continue;
            }

            listener.onEvent(e);
            if(e.bStop())
                break;

            node = node.Next;
        }
	}


    public class UiEvent
    {
        public EventId eid;
        public object data;
        public UiEvent(EventId eid, object data)
        {
            this.eid = eid;
            this.data = data;
        }
    }
    private LinkedList<UiEvent> m_uiEvent = new LinkedList<UiEvent>();
    /**
     * 非UI线程不能调整UI，故放入UI线程更新
     */
    public void dispatchUiEvent(EventId eid, object data)
    {
        m_uiEvent.AddLast(new UiEvent(eid, data));
    }


    public void procUiEvent()
    {
        var node = m_uiEvent.First;
        LinkedListNode<UiEvent> next;
        UiEvent e;
        while (node != null)
        {
            next = node.Next;
            e = node.Value;
            
            m_uiEvent.Remove(node);
            node = next;

            dispatchEvent(e.eid, e.data);
        }
    }


    public void removeAllUnused(){
        foreach (var ls in allListeners.Values)
        {
            var node = ls.First;
            while (node != null)
            {
                if (node.Value.bStop())
                {
                    var next = node.Next;
                    ls.Remove(node);
                    node = next;
                }
                else
                {
                    node = node.Next;
                }
            }
        }
	}


}