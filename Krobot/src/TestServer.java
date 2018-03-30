import java.util.List;

import ServerSearch.Point;
import ServerSearch.Server;

public class TestServer {
	
	public static void main(String [] args) {
		Server server = new Server();
		
		List<Point> list = server.run();
		for(int i=0; i<list.size(); i++) {
			System.out.println(list.get(i));
		}
		
//		while(true) {
//			
//			System.out.println("Fini");
//		}
	}

}
