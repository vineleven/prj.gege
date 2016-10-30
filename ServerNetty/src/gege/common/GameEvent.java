package gege.common;

public class GameEvent {
	
	private Object m_data = null;
	private boolean m_stop = false;
	
	
	public GameEvent() {
	}
	
	
	public GameEvent(Object data) {
		m_data = data;
	}
	
	
	public void stop(){
		m_stop = true;
	}
	
	
	public boolean bStop(){
		return m_stop;
	}
	
	
	public Object getData(){
		return m_data;
	}
	
}
