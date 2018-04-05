import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ServerSearch.Point;
import utils.*;

public class TestSaveCalibration {
	
	public static void main(String args []) throws IOException, ClassNotFoundException {
		
		ArrayList<Tuple<EquationLine,Integer>> equationsLinesColors = new ArrayList<>();
		
		Point p1 = new Point(200,2);
		Point p2 = new Point(-13,4);
		EquationLine e1 = new EquationLine(p1,p2);
		equationsLinesColors.add(new Tuple(e1,1));
		System.out.print(equationsLinesColors.get(0).x.getA());
		System.out.print(equationsLinesColors.get(0).x.getB());
		
		File file = new File("calibration");
			if(!file.exists()){
				file.createNewFile();
			}else{
				file.delete();
				file.createNewFile();
			}
			ObjectOutputStream str = new ObjectOutputStream(new FileOutputStream(file));
			str.writeObject(equationsLinesColors);
			str.flush();
			str.close();
				
		file = new File("calibration");
		if(file.exists()){
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			
		ArrayList<Tuple<EquationLine,Integer>>	equationsLines = (ArrayList<Tuple<EquationLine,Integer>>)ois.readObject(); 
		System.out.print(equationsLines.get(0).x.getA());
		System.out.print(equationsLines.get(0).x.getB());
			ois.close();
		}
	}
}
