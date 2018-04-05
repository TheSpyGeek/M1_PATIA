import ServerSearch.Point;
import utils.EquationLine;

public class TestEquationLine {

	public static void main(String args []) {
		Point p1 = new Point(200,2);
		Point p2 = new Point(-13,4);
		
		Point p3 = new Point(95, 252);
		Point p4 = new Point(95, 0);
		
				
		Point p5 = new Point(204,127);
		Point p6 = new Point(-11,127);
		
		Point p7 = new Point(148, 252);
		Point p8 = new Point(153, 2);
		

		
		EquationLine e1 = new EquationLine(p1,p2);
		e1.printEquationParameters();
		EquationLine e2 = new EquationLine(p3,p4);
		e2.printEquationParameters();
		EquationLine e3 = new EquationLine(p5,p6);
		e3.printEquationParameters();
		EquationLine e4 = new EquationLine(p7,p8);
		e4.printEquationParameters();
		
		Point e = e1.IntersectionWithEquation(e2);
		System.out.println(e.getX()+" "+e.getY());
		
		Point v = new Point(2,1);
		EquationLine e5 = new EquationLine(p1,v,true);
		System.out.println(e5.getA()+" "+e5.getB());
		
		/*  TEST Methode pointIsAbove()*/
		Point p9 = new Point(1,4);
		EquationLine e6 = new EquationLine(1,2);
		System.out.print(e6.pointIsAbove(p9));
		
		System.out.println("TEST : ");
		
				Point p10 = new Point(149, 197);
		Point  vecteur = new Point(0,-20);
		
		EquationLine white = new EquationLine(0,4);
		EquationLine temp = new EquationLine(p10,vecteur,true);
		System.out.println(temp.getA()+" "+temp.getB());
		
		Point f = temp.IntersectionWithEquation(white);
		System.out.println(f.getX()+" "+f.getY());
	}
}
