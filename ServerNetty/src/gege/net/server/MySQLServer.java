package gege.net.server;




public class MySQLServer extends BaseServer {

	@Override
	public void boot() {
	}

	@Override
	public void close() {
	}
	/*
	
	public static void main(String[] args) {

	}
	
	
	
	
	private static MySQLServer inst = null;
	
	
	public synchronized static MySQLServer create(){
		if( inst == null ){
			inst = new MySQLServer();
		}
		
		return inst;
	}
	
	
	public static MySQLServer getInstance(){
		return inst;
	}
	
	
	
	
	SQLConsumer consumer;
	
	public MySQLServer() {
		consumer = new SQLConsumer( Global.MYSQL_UPDATE_INTERVAL );
	}
	
	
	
	@Override
	public void boot(){
		consumer.start();
		
		Logger.info( "===============================================" );
		Logger.info( "  ==========> MySql server start. <==========" );
		Logger.info( "===============================================" );
	}
	
	
	@Override
	public void close() {
		consumer.closeAndSave();
		
		Logger.info( "==================================================" );
		Logger.info( "  ==========> MySql server shutdown. <==========" );
		Logger.info( "==================================================" );
	}
	
	
	
	
	
	class SQLConsumer extends TickThread {
		
		private DBCache cache = new DBCache();
		private long lastCacheSwapTime = 0;
		private boolean willClosed = false;
		
		
		public SQLConsumer( int interval ) {
			super( interval );
		}
		
		
		
		@Override
		public synchronized void start() {
			super.start();
			lastCacheSwapTime = getStartTime();
		}
		
		
		public synchronized void closeAndSave() {
			super.close();
			wakeup();

			// 等待线程结束
			while( !isClosed() ){
				try {
					Thread.sleep( 10 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// 将所有数据放入cache
			mgrDBObject.saveToCache();
			
			willClosed = true;
			for (int i = 0; i < 2; i++) {
				// 重置时间
				lastCacheSwapTime = 0;
				// 主动将cache写入db
				onUpdate();
			}
			
			Logger.info( "Cache has saved completely." );
		}
		
		
		@Override
		public void onUpdate() {
			int count = 0;
			Iterator<Entry<String, Queue<Object[]>>> it = cache.entrySet().iterator();
			Entry<String, Queue<Object[]>> entry;
			String sql;
			Queue<Object[]> queue;
			BatchQueue bq = new BatchQueue();
			OUT:while (it.hasNext()) {
				entry = it.next();
				sql = entry.getKey();
				queue = entry.getValue();

				while( queue.size() > 0 ) {
					int pCount = bq.prepare( queue, Global.MYSQL_UPDATE_COUNT_PER_CONN );
					if ( pCount > 0 ) {
						DBUtil.updateBatch( sql, bq );
						count += pCount;
						// 将要关闭时，将不在进行限制
						if( count >= Global.MYSQL_UPDATE_COUNT_PER_CYCLE && !willClosed )
							break OUT;
					}
				}
			}
			
			if( getLastTickTime() - lastCacheSwapTime > Global.MYSQL_UPDATE_CACHE_INTERVAL ) {
				if( count <= 0 ){
					mgrDBObject.saveToCache();
					cache = mgrShare.getInstance().swapCache( cache );
					lastCacheSwapTime = getLastTickTime();
				}
			}
		}
	}
	*/
}



