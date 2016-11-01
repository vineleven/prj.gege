package gege.game;

public abstract class GameEntity {
	public float x;
	public float y;
	public float w;
	public float h;
	
	
	private boolean m_bDisposed = false;
	
	
	
	
	public void setPosition(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	
	public void dispose(){
		m_bDisposed = true;
	}
	
	
	public boolean isDisposed(){
		return m_bDisposed;
	}
	
	
	final public void update(){
		if(m_bDisposed)
			return;
		
		onUpdate();
	}
	
	
	public abstract void onUpdate();
}
