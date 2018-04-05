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
	
	public static void main(String args []) {
		ArrayList<Tuple<EquationLine,Integer>> equationsLinesColors = new ArrayList<>();
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
		
	}
}
