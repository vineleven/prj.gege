package gege.test;

import gege.consts.Global;






public class TestJava {
	
	static int total = 0;
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		long time = 1456244006;
		
		long cur = System.currentTimeMillis();
		
		long delta = cur / 1000 - time;
		
		
//		167308

		String a = "";
		String[] b = a.split( Global.REGEX_CFG_SPLIT );
		
		System.out.println( "len:" + b.length );
//		for (int i = 0; i < b.length; i++) {
//			System.out.println( " --------> " + b[i] );
//		}
//		
		System.out.println( " --------> end" );
		
	}
	
}
