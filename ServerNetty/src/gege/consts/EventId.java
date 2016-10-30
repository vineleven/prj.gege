package gege.consts;


/**
 * @author vineleven
 *
 */
public enum EventId {
	GLOBAL_REQUEST,
	
	GAME_ROOM_EMPTY,
	
	EVENT_END;
	
	
	
	
	public static int getCount(){ return values().length; }
	
	
}
