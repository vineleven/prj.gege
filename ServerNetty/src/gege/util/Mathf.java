package gege.util;

import java.util.Random;

public class Mathf {
	final private static Random random = new Random( System.currentTimeMillis() );
	
	
	public static float normalPower (float x1, float y1){
		return x1*x1 + y1*y1;
	}
	
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @return [ start, end )
	 */
	public static int randomInt( int start, int end ){
		return random.nextInt( end - start ) + start;
	}
	
	
	/**
	 * @param len
	 * @return 随机[1,len]
	 */
	public static int[] randomArray(int len){
		int[] order = new int[len];
		int rnd;
		for (int i = 1; i <= len; i++) {
			do {
				rnd = randomInt(0, len);
			} while (order[rnd] != 0);
			order[rnd] = i;
		}
		
		return order;
	}
	
	
	/**
	 * 圆饼概率，计算所有的和，然后随机到其中一个(必然会随机到其中一个)
	 * @param rates 概率列表
	 * @return -1 失败
	 */
	public static int getRandomIndexByRound( int ... rates ){
		int sum = 0;
		for (int i = 0; i < rates.length; i++) {
			sum += rates[i];
		}
		
		if( sum <= 0 )
			return -1;
		
		int rand = randomInt( 1, sum );
		for (int i = 0; i < rates.length; i++) {
			rand -= rates[i];
			if( rand <= 0 )
				return i;
		}
		
		return -1;
	}
	
	
	public static boolean rect2PointXYWHIntersect (int Ax0, int Ay0, int Aw0, int Ah0, int Bx0, int By0, int Bw0, int Bh0)
	{
		int Ax1 = Ax0 + Aw0;
		int Ay1 = Ay0 + Ah0;
		int Bx1 = Bx0 + Bw0;
		int By1 = By0 + Bh0;
		
		if(!(Ax0 <= Ax1))Logger.warn("Math_RectIntersect. Ax0 is bigger than Ax1");;
		if(!(Ay0 <= Ay1))Logger.warn("Math_RectIntersect. Ay0 is bigger than Ay1");;
		if(!(Bx0 <= Bx1))Logger.warn("Math_RectIntersect. Bx0 is bigger than Bx1");;
		if(!(By0 <= By1))Logger.warn("Math_RectIntersect. By0 is bigger than By1");;

		if (Ax1 < Bx0)	return false;
		if (Ax0 > Bx1)	return false;
		if (Ay1 < By0)	return false;
		if (Ay0 > By1)	return false;
		return true;
	}
	
	
	public static float clamp(float v, float min, float max){
		if(v < min)
			v = min;
		else if(v > max)
			v = max;
		
		return v;
	}
	
	
	public static float lerp(float from, float to, float t){
		t = clamp(t, 0, 1);
		return from + (to - from) * t;
	}
	
	
	public static float distance(float x1, float y1, float x2, float y2){
		return (float) Math.sqrt(normalPower(x1 - x2, y1 - y2));
	}
}
