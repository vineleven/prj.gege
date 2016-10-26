package gege.test;

public class TestQueueA {
	private int count = 0;
	
	public void add(){
		count++;
	}
	
	
	public void remove(){
		count--;
	}
	
	
	public int size(){
		return count;
	}
}
