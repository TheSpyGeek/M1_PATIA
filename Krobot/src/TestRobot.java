
import java.io.IOException;

import controller.MyController;

public class TestRobot {

	
	public static void main(String args []) {

		MyController controller = new MyController();

		try {
			controller.start();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
