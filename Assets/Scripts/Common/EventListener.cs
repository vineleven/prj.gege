using System;

public interface EventListener {

	/**
	 * 
	 * @param e
	 * @param datas
	 * @return 返回true会中断Event
	 */
	bool onEvent( Object data );
	//	public void mark( int listenerId );
}

