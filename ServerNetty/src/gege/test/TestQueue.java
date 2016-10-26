package gege.test;

import gege.common.SyncQueue;




public class TestQueue {
	static int test = 0;
	static SyncQueue<String> q = new SyncQueue<String>();
	static int count = 1000000;
	public static void main(String[] args) {
		
		put();
		put();
		put();
		put();

		get();
		get();
		get();
		get();
		
	}
	
	
	public static void put(){
		new Thread(
			()->{
				for (int i = 0; i < count; i++) {
					q.enqueue( "1" );
				}
				System.out.println( "put complete" );
			}
		).start();
	}
	
	
	public static void get(){
		new Thread(
			()->{
				for (int i = 0; i < count; i++) {
					q.dequeue();
				}
				System.out.println( "get complete" );
			}
		).start();
	}
	
}
