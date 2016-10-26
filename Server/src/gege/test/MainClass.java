package gege.test;
import java.util.Hashtable;

import javax.swing.plaf.SliderUI;

import gege.game.GameServer;



public class MainClass {
	public static void main(String[] args) {
		Hashtable<String, Object> table = new Hashtable<String, Object>();
		
		boolean[] b = {true, true};
		int count = 10000000;
		new Thread(()->{
			for (int i = 0; i < count; i++) {
				table.put("i" + i, i);
			}
			b[0] = false;
		}).start();
		
		new Thread(()->{
			int i = 0;
			
			while(i < count){
				if(table.remove("i"+i)!=null){
					i++;
				}
			}
			
			b[1] = false;
		}).start();
		
		while (b[0] || b[1]) {
			Thread.yield();
		}
		
		System.out.println("count:" + table.size());
	}
}
