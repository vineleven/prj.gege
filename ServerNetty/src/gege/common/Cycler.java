package gege.common;



/**
 * 循环器
 * <p>
 * 
 * 可以只当做计数器使用
 * <p>
 * 
 * 也可以放入一个执行器
 * <p>
 * 
 * cycle=1 时，每次tick都会返回true
 * 
 * @author vineleven
 *
 */


public class Cycler {
	int cur = 1;
	int cycle = 0;
	Runnable caller = null;
	
	
	/**
	 * 
	 * @param cycle >= 1
	 */
	private Cycler( int cycle ) {
		this.cycle = cycle > 1 ? cycle : 1;
	}
	
	
	/**
	 * 带执行器的循环器
	 * @param cycle >= 1
	 * @param caller 执行器
	 */
	public Cycler( int cycle, Runnable caller ){
		this( cycle );
		
		this.caller = caller;
	}
	
	
	/**
	 * 更新计数器
	 * 
	 * @return 周期是否完成
	 */
	private boolean tick(){
		if( cur >= cycle ){
			cur = 1;
			return true;
		}
		cur++;
		
		return false;
	}
	
	
	/**
	 * 更新执行器
	 */
	public void update(){
		if( tick() )
			if( caller != null )
				caller.run();
	}
}
