package gege.util;

public class Logger {
	public static void debug(String msg) {
		System.out.println( "[debug] " + msg);
	}

	public static void debugf(String formatStr, Object... args) {
		System.out.printf( "[debug] " + formatStr + "\n", args);
	}
	
	public static void info(String msg) {
		System.out.println( "[info] " + msg );
	}
	
	public static void infof( String formatStr, Object... args ) {
		System.out.printf( "[info] " + formatStr + "\n", args );
	}
	
	public static void warn(String msg) {
		System.out.println( "[warn] " + msg );
	}
	
	public static void warnf( String formatStr, Object... args ) {
		System.out.printf( "[warn] " + formatStr + "\n", args );
	}

	public static void error(String msg) {
		System.err.println( "[error] " + msg);
	}
	
	public static void error( String msg, Object keywords ){
		System.err.printf( "[error] %s : [%s].\n", msg, keywords );
	}

	public static void errorf(String formatStr, Object... args) {
		System.err.printf( "[error] " + formatStr + "\n", args);
	}
}
