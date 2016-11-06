package gege.game;

public class GameItem extends GameEntity {
	private static int _nextId = 0;
	
	
	public static int TYPE_RED = 0;
	public static int TYPE_BLUE = 1;
	
	
	// 唯一标识
	final private int m_id;
	
	
	private int m_type;
	
	
	public GameItem(int type) {
		m_id = _nextId++;
		m_type = type;
	}
	
	
	public int getId(){
		return m_id;
	}
	
	
	public int getType(){
		return m_type;
	}
	
	
	public boolean inPos(Vector3 v){
		if(v.x == x && v.y == y)
			return true;
		
		return false;
	}
	

	@Override
	public void onUpdate() {
	}

}
