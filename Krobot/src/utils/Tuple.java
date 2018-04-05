package utils;

import java.io.Serializable;

public class Tuple<X, Y> implements Serializable{ 

	/**
	 * 
	 */
	private static final long serialVersionUID = -8265529959430573584L;
	public final X x; 
	public final Y y;
	
	public Tuple(X x, Y y) { 
	    this.x = x; 
	    this.y = y; 
	  }
	  
	@Override
	public String toString() {
		return x + "|" + y;
	}
} 
