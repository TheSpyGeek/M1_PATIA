import ServerSearch.Point;
import utils.EquationLine;

public class TestEquationLine {

	public static void main(String args []) {
		Point p1 = new Point(1,1);
		Point p2 = new Point(3,2);
		
		EquationLine e1 = new EquationLine(p1,p2);
		System.out.println(e1.getA()+" "+e1.getB());
		
		Point pt1 = new Point(-1,1);
		Point pt2 = new Point(-3,2);
		
		EquationLine e2 = new EquationLine(pt1,pt2);
		System.out.println(e2.getA()+" "+e2.getB());
		
		Point e = e1.IntersectionWithEquation(e2);
		System.out.println(e.getX()+" "+e.getY());
		
		Point v = new Point(2,1);
		EquationLine e3 = new EquationLine(p1,v,true);
		System.out.println(e3.getA()+" "+e3.getB());
	}
}
