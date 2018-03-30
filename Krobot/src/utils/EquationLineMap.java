package utils;

import org.opencv.core.Point;

public class EquationLineMap {
	double a; // Le coefficient directeur
	double b; // L'ordonnée à l'origine 
	
	EquationLineMap(double a, double b){
		this.a = a;
		this.b = b;
	}
	
	EquationLineMap(Point pointOnLine,Point vecteurLine){
		this.a = vecteurLine.y / vecteurLine.x;
		this.b = ( -1 * pointOnLine.x * vecteurLine.y) + pointOnLine.y * vecteurLine.x;
	}
	
	/**
	 * @author duvernet
	 * @param equation
	 * @return Retourne le point d'intersection des deux equations affine
	 * Cette fonction résout le systeme ci-dessous, afin pour trouver le point d'intersection
	 * 	| y = ax + b 
	 * 	| y = mx + p
	 */
	public Point IntersectionWithEquation(EquationLineMap equation) {
		double m = equation.getA();
		double p = equation.getB();
		double x = p - this.b / this.a - m;
		double y = this.a * x + this.b;
		
		return new Point(x,y);
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}
}
