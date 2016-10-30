package gege.common;


public class EventListener {
	public interface OnEvent{
		public void call(GameEvent e);
	}
	
	
	private OnEvent m_callback;
	
	private boolean m_stop = false;
	private boolean m_pause = false;
	
	
	public EventListener(OnEvent callback) {
		m_callback = callback;
	}
	
	
	public void onEvent(GameEvent e){
		m_callback.call(e);
	}
	
	
	public void stop(){
		m_stop = true;
		m_callback = null;
	}
	
	
	public boolean bStop(){
		return m_stop;
	}
	
	
	public void pause(){
		m_pause = true;
	}
	
	
	public void resume(){
		m_pause = false;
	}
	
	
	public boolean bPause(){
		return m_pause;
	}
}
