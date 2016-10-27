package gege.util;

import gege.consts.Global;
import gege.exception.ServerException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class Tools {
	final private static DateFormat dateFormat = new SimpleDateFormat(Global.DATE_FORMAT_STRING);
	final private static Random random = new Random( System.currentTimeMillis() );
	final private static int[] DEFAULT_INT_ARRAY = {};
	final private static long[] DEFAULT_LONG_ARRAY = {};
	
	// private String parseUri(String requestString) {
	// int index1, index2;
	// index1 = requestString.indexOf(' ');
	// if (index1 != -1) {
	// index2 = requestString.indexOf(' ', index1 + 1);
	// if (index2 > index1)
	// return requestString.substring(index1 + 1, index2);
	// }
	// return null;
	// }
	

	public static synchronized String getCurDate() {
		Date date = new Date(System.currentTimeMillis());
		return dateFormat.format(date);
	}
	
	
	public static int[] long2Ints( long number ){
		int[] ints = new int[2];
		for (int i = 0; i < 2; i++) {
			ints[i] = (int) ((number >> ( i * 32 )) & 0xffffffffL );
		}
		
		return ints;
	}
	
	
	public static long ints2Long( int[] ints ){
		long number = 0;
		for (int i = 0; i < ints.length; i++) {
			// 这个转化是必要的
			int temp = ints[i];
			number |= ( ( temp & 0xffffffffL ) << ( i * 32 ) );
		}
		return number;
	}
	
	
	public static byte[] long2Bytes( long number, int count ){
		byte[] bytes = new byte[count];
		for (int i = 0; i < count; i++) {
			bytes[i] = (byte) ((number >> (i * 8)) & 0xff );
		}
		
		return bytes;
	}
	
	
	public static long bytes2Long( byte[] bytes ){
		long number = 0;
		for (int i = 0; i < bytes.length; i++) {
			// 这个转化是必要的
			long temp = bytes[i];
			number |= ( ( temp & 0xff ) << ( i * 8 ) );
		}
		return number;
	}
	
	
	public static byte[] int2Bytes( int number, int count ){
		byte[] bytes = new byte[count];
		for (int i = 0; i < count; i++) {
			bytes[i] = (byte) ((number >> (i * 8)) & 0xff );
		}
		
		return bytes;
	}


	public static byte[] int2Byte(int number) {
		return int2Bytes( number, 4 );
	}


	public static int bytes2Int(byte[] bytes) {
		int number = 0;
		for (int i = 0; i < bytes.length; i++) {
			number |= ( ( bytes[i] & 0xff ) << ( i * 8 ) );
		}
		
		return number;
	}
	
	
	/**
	 * 获取包内所有类(不递归)
	 * @param packageName
	 * @return HashMap
	 */
	public static HashMap<String, Class<?>> getClasses( String packageName ) {
		HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
		List<String> classFiles = getAllFile( packageName, ".class" );
		for( String classFile : classFiles ) {
			String className = classFile.substring( 0, classFile.length() - 6 );
			String file = packageName + "." + className;
			try {
				classes.put( className, Class.forName( file ) );
			} catch ( ClassNotFoundException e ) {
				e.printStackTrace();
			}
		}
		
		return classes;
	}
	
	
	/**
	 * 获取包内指定文件名(不递归，不包含路径)
	 * @param packageName 包名
	 * @param ext 扩展名
	 * @return List
	 */
	public static List<String> getAllFile(String packageName, String ext ) {
		List<String> list = new ArrayList<String>();
		if( packageName == null )
			return list;

		ClassLoader cld = Thread.currentThread().getContextClassLoader();
		if (cld == null)
			return list;
		
		String filePath = packageName.replace('.', '/');
		URL resource = cld.getResource( filePath );
		if (resource == null)
			return list;
		
		File packeageDir = new File( resource.getPath() );
		while( packeageDir.isDirectory() ) {
			String[] allFiles = packeageDir.list();
			if( allFiles == null )
				break;
			
			if( ext == null )
				ext = "";
			
			for ( String file : allFiles ) {
				if( file.endsWith( ext ) ){
					list.add( file );
				}
			}
			break;
		}
		
		return list;
	}
	
	
	/**
	 * 获取文件
	 * @param file 相对路径xx/xx/xxx.xx
	 */
	public static String getFile( String file ){
		Logger.infof( "Get File: %s", file );
		ClassLoader cld = Thread.currentThread().getContextClassLoader();
		InputStream input = cld.getResourceAsStream( file );
		if( input == null ){
			throw new ServerException( "Can't Get File: " + file );
		}
		BufferedReader reader = new BufferedReader( new InputStreamReader( input ) );
		try {
			StringBuffer strBuffer = new StringBuffer();
			String line;
			while( ( line = reader.readLine() ) != null ) {
				strBuffer.append( line );
				strBuffer.append( '\n' );
			}
			return strBuffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	/**
	 * 
	 * @param start
	 * @param end
	 * @return [ start, end )
	 */
	public static int getRandomInt( int start, int end ){
		return random.nextInt( end - start ) + start;
	}
	
	
	public final static int Math_NormPow (int x1, int y1){
		return x1*x1 + y1*y1;
	}
	
	
	/**
	 * 圆桌概率，计算所有的和，然后随机到其中一个(必然会随机到其中一个)
	 * @param rates 概率列表
	 * @return -1 失败
	 */
	public static int getRandomIndexByRound( int[] rates ){
		int sum = 0;
		for (int i = 0; i < rates.length; i++) {
			sum += rates[i];
		}
		
		if( sum <= 0 )
			return -1;
		
		int rand = getRandomInt( 1, sum );
		for (int i = 0; i < rates.length; i++) {
			rand -= rates[i];
			if( rand <= 0 )
				return i;
		}
		
		return -1;
	}
	
	
	public static int[] splitCfgWithInt( String str ){
		if("".equals(str)) return DEFAULT_INT_ARRAY;
		
		String[] strs = str.split( Global.REGEX_CFG_SPLIT );
		int[] result = new int[ strs.length ];
		for (int i = 0; i < strs.length; i++) {
			result[ i ] = Integer.parseInt( strs[ i ] );
		}
		
		return result;
	}
	
	
	public static long[] splitCfgWithLong( String str ){
		if("".equals(str)) return DEFAULT_LONG_ARRAY;
		
		String[] strs = str.split( Global.REGEX_CFG_SPLIT );
		long[] result = new long[ strs.length ];
		for (int i = 0; i < strs.length; i++) {
			result[ i ] = Long.parseLong( strs[ i ] );
		}
		
		return result;
	}
	
	
	public static String longToCfgString(long[] values, int size){
		int min = size <= values.length ? size : values.length;
		if(min <= 0) return Global.REGEX_CFG_SPLIT;
		
		String str = "";
		for (int i = 0; i < min; i++) {
			str += (values[i] + Global.REGEX_CFG_SPLIT);
		}
		
		return str;
	}
	
	
	public static String intToCfgString(int[] values, int size){
		int min = size <= values.length ? size : values.length;
		if(min <= 0) return Global.REGEX_CFG_SPLIT;
		
		String str = "";
		for (int i = 0; i < min; i++) {
			str += (values[i] + Global.REGEX_CFG_SPLIT);
		}
		
		return str;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void mergeMap( HashMap srcMap, HashMap destMap ){
		Iterator<Entry> it = destMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = it.next();
			srcMap.put( entry.getKey(), entry.getValue() );
		}
	}
	
	
	/**
	 * 变长数组的功能
	 */
	public static int[] append( int[] src, int value ){
		int last = src.length;
		src = Arrays.copyOf( src, last + 1 );
		src[last] = value;
		
		return src;
	}
	
	
	public static <T> T[] append( T[] src, T value ){
		int last = src.length;
		src = Arrays.copyOf( src, last + 1 );
		src[last] = value;
		
		return src;
	}
	
	
	
//	public static ByteBuf getSendMsg( String cmd, JSONObject data ){
//		JSONObject j = new JSONObject();
//
//		j.put( Global.MSG_KEY_CMD, cmd );
//		j.put( Global.MSG_KEY_DATA, data );
//		
//		return Unpooled.copiedBuffer( j.toString().concat( Global.MSG_END_FLAG ).getBytes() );
//	}
}
