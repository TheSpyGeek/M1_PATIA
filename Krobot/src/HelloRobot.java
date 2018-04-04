import java.io.IOException;

import controller.MyController;
import lejos.hardware.Button;
import lejos.utility.Delay;

public class HelloRobot {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	/*	try {
			
		System.out.println("Hello World!");
		Button.ENTER.waitForPress();
		
		} catch (Throwable t) {
			t.printStackTrace();
			Delay.msDelay(10000);
			System.exit(0);
		}*/
		
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