package gege.common;

public abstract class TickThread extends Thread {
	
	private int interval = 0;
	private boolean isRunning = false;
	private boolean isClosed = false;
	private Object _lock = new Object();
	
	// 不能为空值
	private long startTime = System.currentTimeMillis();
	private long lastTickTime = startTime;
	
	
	
	public TickThread(){
		this(0);
	}
	
	
	public TickThread( int interval ) {
		this.interval = interval;
	}
	
	
	protected void setInterval( int interval ){
		this.interval = interval <= 0 ? 0 : interval;
	}
	
	
	protected int getInterval(){
		return interval;
	}
	
	
	public synchronized boolean isRunning(){
		return isRunning;
	}
	
	
	public synchronized boolean isClosed(){
		return isClosed;
	}
	
	
	protected long getStartTime(){
		return startTime;
	}
	
	
	protected long getLastTickTime(){
		return lastTickTime;
	}
	
	
	final protected void setLastTickTime( long time ){
		lastTickTime = time;
	}
	
	
	
	
	@Override
	public synchronized void start() {
		if( isRunning )
			return;

		isRunning = true;
		startTime = System.currentTimeMillis();
		lastTickTime = startTime;
		super.start();
	}
	
	
	public synchronized void close(){
		isRunning = false;
	}
	
	
	public void wakeup(){
		synchronized( _lock ){
			_lock.notify();
		}
	}
	
	
	@Override
	public void run() {
		lastTickTime = startTime;
		
		long curTime = 0, sleepTime = 0;
		while ( isRunning ) {
			onUpdate();
			
			if( interval > 0 ){
				curTime = System.currentTimeMillis();
				sleepTime = interval - ( curTime - lastTickTime );
				
				if( sleepTime > 0 ){
					try {
						synchronized( _lock ) {
							_lock.wait( sleepTime );
						}
//						sleep( sleepTime );
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			lastTickTime = System.currentTimeMillis();
		}
		
		isClosed = true;
	}

	
	public abstract void onUpdate();
	
}
