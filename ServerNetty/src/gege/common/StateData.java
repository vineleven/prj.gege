package gege.common;


/**
 * 
 * 使用前先确定状态是否一致
 * @author vineleven
 *
 */
final public class StateData {
	public int int1 = 0;
	public int int2 = 0;
	public int int3 = 0;
	
	
	public StateData(int i1){
		this(i1, 0);
	}
	
	
	public StateData(int i1, int i2) {
		this(i1, i2, 0);
	}
	
	
	public StateData(int i1, int i2, int i3) {
		int1 = i1;
		int2 = i2;
		int3 = i3;
	}
}
