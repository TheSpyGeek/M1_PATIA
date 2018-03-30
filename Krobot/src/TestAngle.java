import java.util.Vector;

public class TestAngle {
	
	public static void main(String []args) {
		Vector<Integer> v1 = new Vector<>();
		Vector<Integer> v2 = new Vector<>();
		
		v1.add(1);
		v1.add(1);
		
		v2.add(-1);
		v2.add(-2);
		
		System.out.println("v1 size "+ v1.size() );
		System.out.println("v2 size "+ v2.size() );
		
		if((dotProduct(v1, v2) * crossProduct(v1,  v2)) > 0) {
			System.out.println("Est a gauche");
		} else {
			System.out.println("Est a droite");
		}
		
	}
	
	
	public static int dotProduct(Vector<Integer> a, Vector<Integer> b) {
		if(a.size() != b.size()); // throw an exception of some kind

		int i; 
		Integer sum=0;  
		
		for(i=0; i<a.size(); i++) 
			sum += a.get(i) * b.get(i); 
		return sum;
	}
	
	public static float crossProduct(Vector<Integer> a, Vector<Integer> b){
		
		return (a.get(0)*b.get(1) - a.get(1) * b.get(0));
	}

}
