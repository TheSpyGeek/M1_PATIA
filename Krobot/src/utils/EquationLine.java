package utils;

import ServerSearch.Point;
import java.io.Serializable;

public class EquationLine implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9177158113600041557L;
	double a; // Le coefficient directeur
	double b; // L'ordonnée à l'origine 
	
	/**
	 * Calcul une equation selon deux points donnés
	 * 
	 * @author duvernet
	 * @param p1 un point du plan
	 * @param p2 un point du plan
	 * 
	 */
	public EquationLine(Point p1, Point p2){
		double p1X = (double) p1.getX();
		double p1Y = (double) p1.getY();
		
		double p2X = (double) p2.getX();
		double p2Y = (double) p2.getY();
		
		this.a = (p2Y- p1Y) / (p2X - p1X);
		this.b = p1Y - this.a * p1X;
	}
	/**
	 * Crée une equation affine en fonction du coefficiant directeur
	 * et de l'ordonée à l'origine
	 * 
	 * @author duvernet
	 * @param a Le coefficiant directeur de la droite 
	 * @param b L'ordonnée à l'origine de la droite
	 */
	public EquationLine(double a, double b){
		this.a = a;
		this.b = b;
	}
	/**
	 * Calcul une equation de droite affine en fonction d'un point et d'un vecteur de cette droite
	 * Démonstration :
	 *  On a un point A(j,k) et un vecteur U(t,u)
	 * 	Soit une equation cartesienne de la forme ax + by + c = 0 
	 * 			avec a = u , b= -t , c = tj - uk 
	 * 	
	 * On peut donc écrire l'équation sous la forme y = (-a/b)*x - c/b
	 * 
	 * @author duvernet
	 * @param pointOnLine 
	 * @param vecteurLine
	 * @param equationWithVector Variable inutile, elle juste présente pour avoir deux constructeur 
	 * 		  avec la même entête mais qui calcul l'equation de manière différente
	 * 
	 */
	public EquationLine(Point pointOnLine,Point vecteurLine,boolean equationWithVector){
		double vecteurX = (double) vecteurLine.getX();
		double vecteurY = (double) vecteurLine.getY();
		double pointOnLineX = (double) pointOnLine.getX();
		double pointOnLineY = (double) pointOnLine.getY();
		this.a = vecteurX != 0 ? vecteurY / vecteurX : 0;
		//this.b = (vecteurX * pointOnLineX - vecteurY * pointOnLineY)/ vecteurX; 
		this.b = pointOnLineX - (this.a * pointOnLineY);
	}
		
	/**
	 * Résout le systeme ci-dessous, afin pour trouver le point d'intersection
	 * 	| y = ax + b 
	 * 	| y = mx + p
	 * 
	 * @author duvernet
	 * @param equation
	 * @return Retourne le point d'intersection des deux equations affines
	 */
	public Point IntersectionWithEquation(EquationLine equation) {
		double m = equation.getA();
		double p = equation.getB();
		double x = (p - this.b) / (this.a - m);
		double y = (this.a * x) + this.b;
		
		return new Point((int)x,(int)y);
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
	
	public void printEquationParameters() {
		System.out.println("Coefficiant directeur:"+this.a);
		System.out.println("Ordonnée à l'origine:"+this.b);
	}
	
	public boolean pointIsAbove(Point p) {
		
		return p.getY() >= this.a * p.getX() + this.b;
	}
}
