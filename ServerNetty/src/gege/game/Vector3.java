package gege.game;

public class Vector3 {
	public float x;
	public float y;
	public float z;
	
	public Vector3() {
	}
	
	public Vector3(float x, float y) {
		this(x, y, 0);
	}
	
	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void add(Vector3 v){
		x += v.x;
		y += v.y;
		z += v.z;
	}

	public float magnitude(){
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	
//    public static Vector3 Lerp(Vector3 from, Vector3 to, float t)
//    {
//    	
//    }
	
	
	
	
}
