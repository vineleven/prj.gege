package gege.impl;




public interface EventListener {
	
	/**
	 * 
	 * @param e
	 * @param datas
	 * @return 返回true会中断Event
	 */
	public boolean onEvent( Object ... datas );
//	public void mark( int listenerId );
}

