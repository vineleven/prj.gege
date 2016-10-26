package gege.consts;


/**
 * @author vineleven
 *
 */
public enum Event {
	 RES_UPER_LVUP
//	,HERO_QUALITY_UPER_LVUP
//	,RES_CAPACITY_UPDATE		// 资源增加

	,HERO_COME_BACK
	
	,FLUSH_RECURIT				// 更新招募的士兵
	
	,SERVER_SHUTDOWN
	;
	
	
	
	
	
	
	public int getId() { return ordinal(); }
	public static int getCount(){ return values().length; }
}
