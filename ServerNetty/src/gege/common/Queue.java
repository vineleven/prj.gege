package gege.common;

import java.util.Arrays;




/**
 * 列队
 * @author vineleven
 *
 */
public class Queue<T> {
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	private boolean autoGrow = true;
	private int maxLen;
	private int tail;
	private int head;
	private int count;
	private Object[] queue;
	
	
	public Queue(){
		this( 128, true );
	}
	
	
	public Queue( int max ){
		this( max, false );
	}
	
	
	public Queue( int max, boolean autoGrow ) {
		this.autoGrow = autoGrow;
		this.maxLen = max;
		this.tail = 0;
		this.head = 0;
		this.count = 0;
		this.queue = new Object[ max ];
	}
	
	
	public int size(){
		return count;
	}
	
	
	public int max(){
		return maxLen;
	}
	
	
	public boolean enqueue( T obj ){
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
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	public T dequeue(){
		while( count <= 0 ){
			return null;
		}
		
		T t = (T)queue[ head ];
		queue[ head ] = null;
		head = ++head % maxLen;
		count--;
		return t;
	}
	
	
	private void grow(){
		int newLen = maxLen + ( maxLen >> 1 );
		maxLen = hugeLen( newLen );
		queue = Arrays.copyOf( queue, maxLen );
	}
	
	
    private static int hugeLen(int len ) {
        if ( len < 0)
            throw new OutOfMemoryError();
        return len > MAX_ARRAY_SIZE ? MAX_ARRAY_SIZE : len;
    }
    
    
    public void clear(){
		tail = 0;
		head = 0;
		count = 0;
		
		for (int i = 0; i < queue.length; i++) {
			queue[i] = null;
		}
    }
}
