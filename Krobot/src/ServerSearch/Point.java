package ServerSearch;

public class Point {
	
	private int x;
	private int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		String p = "{";
		p += this.x;
		p += ", "+ y;
		p += "}";
		return p;
	}

}
