package gege.game;

public abstract class GameEntity {
	public float x;
	public float y;
	private float w = 0.6f;
	private float h = 0.6f;
	
	// 用于碰撞
	private float halfw = w / 2;
	private float halfh = h / 2;
	
	
	protected int m_state;
	
	
	private boolean m_bDisposed = false;
	
	
	
	public void setSize(float w, float h){
		this.w = w;
		this.h = h;
		halfw = w / 2;
		halfh = h / 2;
	}
	
	
	
	protected void setPosition(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	
	protected void setNextState(int state){
		m_state = state;
	}
	
	
	public boolean inState(int state){
		return m_state == state;
	}
	
	
	public boolean tryCollision(GameEntity entity){
		if(x - halfw > entity.x + entity.halfw)
			return false;
		if(x + halfw < entity.x - entity.halfw)
			return false;
		if(y - halfh > entity.y + entity.halfh)
			return false;
		if(y + halfh < entity.y - entity.halfh)
			return false;
		
		return true;
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
