package gege.game;

public class GameItem extends GameEntity {
	
	private int m_type;
	
	
	public GameItem(int type) {
		m_type = type;
	}
	
	
	public int getType(){
		return m_type;
	}
	

	@Override
	public void onUpdate() {
	}

}
