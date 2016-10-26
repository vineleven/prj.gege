package gege.common;

import java.util.ArrayList;
import java.util.Arrays;




/**
 * 同步列队
 * @author vineleven
 *
 */
public class SyncQueue<T> {
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	private int maxLen;
	private boolean autoGrow;
	private int tail;
	private int head;
	private int count;
	private Object[] queue;
	
	
	public SyncQueue(){
		this( 128 );
	}
	
	
	public SyncQueue( int max ) {
		this( max, false );
	}
	
	
	public SyncQueue( int max, boolean autoGrow ){
		this.maxLen = max;
		this.autoGrow = autoGrow;
		this.tail = 0;
		this.head = 0;
		this.count = 0;
		this.queue = new Object[ max ];
	}
	
	
	public synchronized int size(){
		return count;
	}
	
	
	public int max(){
		return maxLen;
	}
	
	
	public synchronized void enqueue( T obj ){
		while( !enqueueImm( obj ) ){
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}
	
	
	public synchronized boolean enqueueImm( T obj ){
		if( autoGrow ){
			if( count >= maxLen - 1 ){
				grow();
			}
		} else {
			if( count >= maxLen ){
				return false;
			}
		}
		
		queue[ tail ] = obj;
		tail = ++tail % maxLen;
		count++;
		notifyAll();
		return true;
	}
	
	
	private void grow(){
		int newLen = maxLen + ( maxLen >> 1 );
		maxLen = hugeLen( newLen );
		
		queue = Arrays.copyOf( queue, maxLen );
	}
	
	
    private static int hugeLen(int len ) {
        if ( len < 0 )
            throw new OutOfMemoryError();
        return len > MAX_ARRAY_SIZE ? MAX_ARRAY_SIZE : len;
    }
	
	
	public synchronized T dequeue(){
		T t;
		while( ( t = dequeueImm() ) == null ){
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		
		return t;
	}
	
	
	@SuppressWarnings("unchecked")
	public synchronized T dequeueImm(){
		if( count <= 0 ){
			return null;
		}
		
		T t = (T)queue[ head ];
		queue[ head ] = null;
		head = ++head % maxLen;
		count--;
		notifyAll();
		return t;
	}
	
	
	public ArrayList<T> dequeue( int num ){
		ArrayList<T> list = new ArrayList<T>( num );
		for (int i = 0; i < num; i++) {
			list.add( dequeue() );
		}
		
		return list;
	}
}
