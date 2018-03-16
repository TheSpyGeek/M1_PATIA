package ServerSearch;

public class Point implements Comparable<Point> {
	
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

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int compareTo(Point p) {
		return getX() < p.getX() ? -1 : (getY() < p.getY()) ? 1 : 0;
	}

}
