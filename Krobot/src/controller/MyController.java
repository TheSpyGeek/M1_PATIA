package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ServerSearch.Point;
import ServerSearch.Server;
import lejos.hardware.Button;
import lejos.robotics.Color;
import linker.ParserPDDL4J;
import motors.Graber;
import motors.Propulsion;
import motors.TImedMotor;
import sensors.ColorSensor;
import sensors.PressionSensor;
import sensors.VisionSensor;
import utils.ArrayIndexComparator;
import utils.EquationLine;
import utils.R2D2Constants;
import utils.Tuple;
import vue.InputHandler;
import vue.Screen;

public class MyController {

	protected ColorSensor    color      = null;
	protected Propulsion     propulsion = null;
	protected Graber         graber     = null;
	protected PressionSensor pression   = null;
	protected VisionSensor   vision     = null;
	protected Screen         screen     = null;
	protected InputHandler   input      = null;
	protected Server		 server		= null;
	protected ParserPDDL4J	 parser		= null;
	protected List<Point> nodesPosition = null;
	protected Point		  robotPosition	= null;
	protected Point		   robotVecteur	= null;
	protected EquationLine    lineRobot = null;
	protected boolean		 top 		= false;
	protected int oldColor,cptColor;
	
	protected ArrayList<Tuple<EquationLine,Integer>> equationsLinesColors;
	private ArrayList<TImedMotor> motors = new ArrayList<TImedMotor>();

	public MyController(){
		propulsion = new Propulsion();
		graber     = new Graber();
		color      = new ColorSensor();
		pression   = new PressionSensor();
		vision     = new VisionSensor();
		screen     = new Screen();
		input      = new InputHandler(screen);
		server	   = new Server();
		parser	   = new ParserPDDL4J();
		nodesPosition = new ArrayList<Point>();
		equationsLinesColors = new ArrayList<>();
	    oldColor = -1;
	    cptColor = 0;
		top = false;

		motors.add(propulsion);
		motors.add(graber);
	}
	
	/**
	 * Lance le robot.
	 * Dans un premier temps, effectue une calibration des capteurs.
	 * Dans un second temps, lance des tests
	 * Dans un troisième temps, démarre la boucle principale du robot pour la 
	 * persycup
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void start() throws IOException, ClassNotFoundException{
		loadCalibration();
		calibration();
		saveCalibration();

		screen.drawText("Lancer", "Ok pour run");
		if(input.isThisButtonPressed(input.waitAny(), Button.ID_ENTER)){
			runIA();
		}
		cleanUp();
	}

	/**
	 * Initialise les equations correspondant aux lignes du plateau à l'aide
	 * de deux palets. La position des deux palets permettent de calculer une equation
	 * de droite
	 * 
	 * @return True si la calibration c'est bien passé sinon False
	 */
	private boolean calibrateNodeEquationLine() {
		screen.drawText("Calibration Lignes", 
		"Appuyez sur echap ","pour skipper");			
		if(!input.waitOkEscape(Button.ID_ESCAPE)){
			color.lightOn();

			//Calibration equation ligne blanche
			screen.drawText("Placer deux palets","sur la ligne blanche","la plus proche","de l'origine");
			input.waitAny();
			addEquationLineFromPalet(Color.WHITE);
			
			//calibration equation deuxième ligne blanche 
			screen.drawText("Placer deux palets","sur la ligne blanche","la plus éloignée"," de l'origine");
			input.waitAny();
			addEquationLineFromPalet(Color.WHITE);
			
			//calibration equation ligne noir 
	/*		screen.drawText("Placer deux palets","sur la ligne noir","parallèle à","l'axe des ordonnées");
			input.waitAny();
			addEquationLineFromPalet(Color.BLACK);
			
			//calibration equation deuxième ligne noir 
			screen.drawText("Placer deux palets","sur la ligne noir","perpandiculaire à"," l'axe des ordonnées");
			input.waitAny();
			addEquationLineFromPalet(Color.BLACK);*/
			
			//calibration equation ligne rouge 
			screen.drawText("Placer deux palets","sur la ligne rouge");
			input.waitAny();
			addEquationLineFromPalet(Color.RED);
			return true;
		}
		return false;
	}
	
	/**
	 * @author duvernet
	 * Recupère les positions des deux palets et calcul l'equation de la droite.
	 * Associe et stock l'equation avec la couleur en paramètre 
	 * @param color La couleur de la ligne à calibrer
	 */
	private void addEquationLineFromPalet(int color) {
		this.server = new Server();
		List<Point> listPalets = server.run();
		if(listPalets.size() !=2) {
			System.out.println("Error bad number of palet on table");
			return;
		}
		
		Collections.sort(listPalets);   
		Point p1 = listPalets.get(0);
		Point p2 = listPalets.get(1);
		EquationLine equation = new EquationLine(p1,p2);
		equationsLinesColors.add(new Tuple<>(equation,new Integer(color)));
		//equation.printEquationParameters();
	}
	
	/**
	 * Calcul de la nouvelle position du robot en fonction de l'equation de la droite de couleur
	 * qu'il a croisé et de l'equation de sa propre droite. Si il croise une droite blanche alors
	 * on détermine la quelle il a croisé à l'aide de son vecteur de direction. Même chose pour les
	 * lignes noires
	 * 
	 * @author duvernet
	 */ 
	private void updatePositionRobotWithLine(int c) {
		int currentColor = c;
		if (currentColor == -1)
			currentColor = color.getCurrentColor();
		System.out.println("Get la couleur"+currentColor);
		switch (currentColor) {
			// Le robot a croisé une des lignes 
            case Color.WHITE:
            	System.out.println("Robot Vecteur x="+robotVecteur.getX()+" Y="+robotVecteur.getY());
            	// Le robot était en direction de la ligne blanche la plus proche de l'origine
                if(robotVecteur.getY() < 0) {
                	System.out.println("Ligne blanche 1");
                	if(equationsLinesColors.get(0).y == Color.WHITE) {
                		lineRobot = new EquationLine(robotPosition,robotVecteur,true);
                		robotPosition = lineRobot.IntersectionWithEquation(equationsLinesColors.get(0).x);
                		lineRobot.printEquationParameters();
                		equationsLinesColors.get(0).x.printEquationParameters();
                		System.out.println("Robot position =" + robotPosition.getX()+" "+robotPosition.getY());
                	}
                }else {
                	System.out.println("Ligne blanche 2");
                	if(equationsLinesColors.get(1).y == Color.WHITE) {
                		lineRobot = new EquationLine(robotPosition,robotVecteur,true);
                		robotPosition = lineRobot.IntersectionWithEquation(equationsLinesColors.get(1).x);
                		System.out.println("Robot position =" + robotPosition.getX()+" "+robotPosition.getY());
                	}
                }
            	break;
            // Le robot à croisé la ligne rouge
            case Color.RED:
            	System.out.print("Ligne red: ");

                if(equationsLinesColors.get(2).y == Color.RED) {
                	lineRobot = new EquationLine(robotPosition,robotVecteur,true);
                	robotPosition = lineRobot.IntersectionWithEquation(equationsLinesColors.get(2).x);
                	System.out.println(robotPosition.getX()+" "+robotPosition.getY());
                }
            	break;
            default:          
            	break;
        }	
	}
		
	private void calibrateNodePosition() {
		screen.drawText("Calibration", "Mettre tout les", 
			"palets sur leur","position",
			"Echap pour skip");
		if(!input.waitOkEscape(Button.ID_ESCAPE)){
			this.server = new Server();
			List<Point> tmp = server.run();
			if (tmp.size() != 9){
				System.out.println("Error bad number of palet on table");
				return;
			}
			Collections.sort(tmp);
			for(int i=0; i<3; i++){
				Point a = tmp.get(i*3);
				Point b = tmp.get(i*3+1);
				Point c = tmp.get(i*3+2);
				if (a.getY() < b.getY() && a.getY() < c.getY()){
					this.nodesPosition.add(i*3, a);
					if (b.getY() < c.getY()){
						this.nodesPosition.add((i*3)+1, b);
						this.nodesPosition.add((i*3)+2, c);
					}
					else{
						this.nodesPosition.add((i*3)+1, c);
						this.nodesPosition.add((i*3)+2, b);
					}
				} else if (b.getY() < a.getY() && b.getY() <c.getY()){
					this.nodesPosition.add(i*3, b);
					if (a.getY() < c.getY()){
						this.nodesPosition.add((i*3)+1, a);
						this.nodesPosition.add((i*3)+2, c);
					}
					else{
						this.nodesPosition.add((i*3)+1, c);
						this.nodesPosition.add((i*3)+2, a);
					}
				} else {
					this.nodesPosition.add(i*3, c);
					if (a.getY() < b.getY()){
						this.nodesPosition.add((i*3)+1, a);
						this.nodesPosition.add((i*3)+2, b);
					}
					else{
						this.nodesPosition.add((i*3)+1, b);
						this.nodesPosition.add((i*3)+2, a);
					}
				}
			}
		}
	}
	
	private void calibrateRobotPositionAndVector() {
		screen.drawText("Calibration", "Calibration robot", "OK si Top autre", "si bas");
		this.top = input.isThisButtonPressed(input.waitAny(), Button.ID_ENTER);
		this.server = new Server();
		List<Point> tmp = server.run();
		Collections.sort(tmp);
		this.robotPosition = tmp.get(0);
		for (Point p : tmp) {
			if(top && this.robotPosition.getY() < p.getY()) {
				this.robotPosition = p;
			} else if(!top && this.robotPosition.getY() > p.getY()) {
				this.robotPosition = p;
			}
		}
		Point far;
		Point far1 = nodesPosition.get(2);
		Point far2 = nodesPosition.get(5);
		Point far3 = nodesPosition.get(8);
		if (top){
			far1 = nodesPosition.get(0);
			far2 = nodesPosition.get(3);
			far3 = nodesPosition.get(6);
		}
		if (Math.abs(far1.getX()-robotPosition.getX()) < Math.abs(far2.getX()-robotPosition.getX())){
			if (Math.abs(far1.getX()-robotPosition.getX()) < Math.abs(far3.getX()-robotPosition.getX())){
				far = far1;
			} else {
				far = far3;
			}
		} else if (Math.abs(far2.getX()-robotPosition.getX()) < Math.abs(far3.getX()-robotPosition.getX())){
			far = far2;
		} else {
			far = far3;
		}
		robotVecteur = new Point(far.getX() - this.robotPosition.getX(), far.getY() - this.robotPosition.getY());
		lineRobot = new EquationLine(robotPosition,robotVecteur,true);
	}
	
//	public static double angleBetweenPoints(Point a, Point b) {
//        double angleA = angleFromOriginCounterClockwise(a);
//        double angleB = angleFromOriginCounterClockwise(b);
//        return Math.abs(angleA-angleB);
//    }
//
//    public static double angleFromOriginCounterClockwise(Point a) {
//        double degrees = Math.toDegrees(Math.atan(a.getY()/a.getX()));
//        if(a.getX() < 0.0) return degrees+180.0;
//        else if(a.getY() < 0.0) return degrees+360.0;
//        else return degrees;
//    }
//
//    public static void main(String[] args) {
//        Point p1 = new Point(1, 100);
//        Point p2 = new Point(-100, 1);
//        System.out.println(angleBetweenPoints(p1, p2));
//    }
	
	/* moves 
	 * 
	 * 
	 * 
	 * 
moverobot1 a d
moverobot1 d g
getpalet g p3
movepaletandrobot2 g d p3
movepaletandrobot2 d a p3
releasepalet a p3
moverobot1 a d
getpalet d p0
movepaletandrobot1 d e p0
movepaletandrobot2 e b p0
releasepalet b p0
moverobot1 b e
moverobot1 e h
getpalet h p4
movepaletandrobot2 h e p4
movepaletandrobot2 e b p4
releasepalet b p4
moverobot1 b e
getpalet e p1
movepaletandrobot2 e b p1
releasepalet b p1
moverobot1 b c
moverobot1 c f
getpalet f p2
movepaletandrobot2 f c p2
releasepalet c p2

	 * 
	 * 
	 * */
	
	private void runIAPDDL() {
		
		// TODO lier les palets du PDDL et la position des palets
		
		
		List<String> moveToDo;
		
		try {
			moveToDo = parser.runProblem();
			
			/* tant qu'il reste des actions à effectuer */
			while (!moveToDo.isEmpty()){
				
				String [] moveParsed = moveToDo.get(0).split(" ");
				
				if(moveParsed.length < 3) {
					System.err.println("Erreur de split de l'action à réaliser");
				}
				
				switch(moveParsed[0]) {
					case "moverobot1":
						if (moveParsed.length != 3) {
							System.err.println("PDDL probleme mauvais nombre d'arguments moverobot");
						} else {
							System.out.println("Le robot se déplace du noeud "+moveParsed[1] + " au noeud "+moveParsed[2]);
						}						
						break;
					case "getpalet":
						if(moveParsed.length != 3) {
							System.err.println("PDDL mauvais nombre arguments getpalet");
						} else {
							System.out.println("Le robot se déplace jusqu'au noeud "+moveParsed[1]+ " et prend le palet "+moveParsed[2]);
						}
						break;
					case "releasepalet":
						if(moveParsed.length != 3) {
							System.err.println("PDDL mauvais nombre arguments releasePalet");
						} else {
							System.out.println("Le robot se déplace jusqu'au noeud "+moveParsed[1]+ " (BUT) et lache le palet "+moveParsed[2]);
						}
						break;
					default:
				}
				
			}
			
		} catch (IOException e) {
			System.err.println("Erreur lors du run problem");
			e.printStackTrace();
		}
		
	
		
	}

	private void runIA() {
//		List<Integer> nodesWithPalet = getNodesWithPalet(server.run());
		List<Point> paletNotInCamp = getPaletNotInCamp(server.run());
		
		while (!paletNotInCamp.isEmpty()){
			//List<Integer> nodesWithPaletCloseFromRobot = getNodesWithPaletCloseFromRobot(nodesWithPalet, 5);
			//parser.parse(nodesWithPaletCloseFromRobot, getNodeWithRobot(), true);
			
			//On récupère les actions à effectué !
//			Point paletToGet = nodesPosition.get(nodesWithPalet.get(0));
//			Point paletToGet = paletNotInCamp.get(0);
			Point paletToGet = getPaletClosestFromRobot(paletNotInCamp);
//			System.out.println("Palet to get : "+paletToGet);
			Point vRobPal = new Point(paletToGet.getX() - this.robotPosition.getX(), paletToGet.getY() - this.robotPosition.getY());
			double angleToRotate = angleCalculation(paletToGet);
			double zproduct = (vRobPal.getX() * robotVecteur.getY()) - (vRobPal.getY() * robotVecteur.getX());
			double dotProd = (vRobPal.getX() * robotVecteur.getX()) + (vRobPal.getY() * robotVecteur.getY());
			boolean turnLeft = (zproduct * dotProd) < 0;
//			System.out.println("zprod = "+zproduct + " dotProd = "+dotProd + " turnLeft = "+turnLeft);
			angleToRotate = Math.abs(angleToRotate);
//			System.out.println("Turn Left = "+turnLeft);
			
			propulsion.rotate((float)angleToRotate, turnLeft, false);
			if(graber.isClose())
				graber.open();
			while(propulsion.isRunning()){
				propulsion.checkState();
				graber.checkState();
				if(input.escapePressed())
					return;
			}
			propulsion.run(true);
			while(propulsion.isRunning() && !pression.isPressed()){
				propulsion.checkState();
				if(input.escapePressed())
					return;
			}
			propulsion.stopMoving();
			graber.close();
			while (graber.isRunning()){
				graber.checkState();
			}
			
			
			double normeRobPal = Math.sqrt(Math.pow(vRobPal.getX(), 2) + Math.pow(vRobPal.getY(), 2));
			Tuple<Double,Double> vectorTmp = new Tuple<Double,Double>((-vRobPal.getX()/normeRobPal), (-vRobPal.getY()/normeRobPal));
			robotPosition = new Point((int)((10*vectorTmp.x) + paletToGet.getX()), (int)((10*vectorTmp.y) + paletToGet.getY()));
			System.out.println("Robot Position="+robotPosition);
			
			robotVecteur = vRobPal;
			
			Point toHome = new Point(robotPosition.getX(), top ? robotPosition.getY() - 20 : robotPosition.getY() + 20);
			System.out.println("Palet to get : "+paletToGet);
			Point vRobHome = new Point(toHome.getX() - this.robotPosition.getX(), toHome.getY() - this.robotPosition.getY());
			angleToRotate = angleCalculation(toHome);
			zproduct = (vRobHome.getX() * robotVecteur.getY()) - (vRobHome.getY() * robotVecteur.getX());
			dotProd = (vRobHome.getX() * robotVecteur.getX()) + (vRobHome.getY() * robotVecteur.getY());
			turnLeft = (zproduct * dotProd) < 0;
//			System.out.println("zprod = "+zproduct + " dotProd = "+dotProd + " turnLeft = "+turnLeft);
			angleToRotate = Math.abs(angleToRotate);
//			System.out.println("Turn Left = "+turnLeft);
			
			propulsion.rotate((float)angleToRotate, turnLeft, false);
			while(propulsion.isRunning()){
				propulsion.checkState();
				if(input.escapePressed())
					return;
			}
			robotVecteur = vRobHome;
			int currentColor;
			propulsion.run(true);
			while(propulsion.isRunning() && (currentColor = color.getCurrentColor()) != Color.WHITE){
				
				if(oldColor == currentColor) {
					cptColor++;
				}
				oldColor = currentColor;
				if(cptColor >= 1) {
						updatePositionRobotWithLine(oldColor);
					    cptColor = 0;
				}
						
				propulsion.checkState();
				if(input.escapePressed())
					return;
			}
			propulsion.stopMoving();
			updatePositionRobotWithLine(Color.WHITE);
			System.out.println("POSITION COURANTE :"+robotPosition);
			
			graber.open();
			propulsion.run(true);
			while (vision.getRaw()[0] < R2D2Constants.COLLISION_DISTANCE);
			propulsion.stopMoving();
			propulsion.run(false);
			while(propulsion.isRunning() && color.getCurrentColor() != Color.WHITE){
				propulsion.checkState();
			}
			propulsion.stopMoving();
			
			
			paletNotInCamp = getPaletNotInCamp(server.run());
		}
	}

	private Point getPaletClosestFromRobot(List<Point> paletNotInCamp) {
		int delta = 1000;
		Point tmpClosest = null;
		int robotX = this.robotPosition.getX();
		int robotY = this.robotPosition.getY();
		for(int i=0; i<paletNotInCamp.size(); i++){
			Point p = paletNotInCamp.get(i);
			int tmpDelta = Math.abs(robotX - p.getX()) + Math.abs(robotY - p.getY());
			if (tmpDelta < delta){
				delta = tmpDelta;
				tmpClosest = p;
			}
		}
		return tmpClosest;
	}

	private double angleCalculation(Point paletToGet) {
//		System.out.println("PaletToGet" + paletToGet);
		Point vRobPal = new Point(paletToGet.getX() - this.robotPosition.getX(), paletToGet.getY() - this.robotPosition.getY());
		double normRabPal = Math.sqrt(Math.pow(vRobPal.getX(), 2) + Math.pow(vRobPal.getY(), 2));
		double normRobVec = Math.sqrt(Math.pow(robotVecteur.getX(), 2) + Math.pow(robotVecteur.getY(), 2));
		double degree = Math.toDegrees(Math.acos(((vRobPal.getX()*robotVecteur.getX()) + (vRobPal.getY()*robotVecteur.getY())) / (normRabPal * normRobVec)));
		if ((robotVecteur.getX()*-vRobPal.getY()) - robotVecteur.getY()*vRobPal.getX() < 0){
			degree = - degree;
		}
//		System.out.println("Degree to turn = "+degree);
		return degree;
	}
	
	private char getNodeWithRobot() {
		int robotX = this.robotPosition.getX();
		int robotY = this.robotPosition.getY();
		int delta = 1000;
		int index = -1;
		for(int i=0; i<this.nodesPosition.size(); i++){
			Point p = this.nodesPosition.get(i);
			int tmpDelta = Math.abs(robotX - p.getX()) + Math.abs(robotY - p.getY());
			if (tmpDelta < delta){
				tmpDelta = delta;
				index = i;
			}
		}
		return (char) (index+65);
	}

	private List<Point> getPaletNotInCamp(List<Point> paletsPositions) {
		List<Point> paletNotInCamp = new ArrayList<Point>();
		for(Point palet : paletsPositions){
			if (!paletIsInCamp(palet)){
				paletNotInCamp.add(palet);
				System.out.println("Palet NOT In camp :"+palet);
			} else {
				System.out.println("Palet In camp :"+palet);
			}
		}
		return paletNotInCamp;
	}
	
//	private List<Integer> getNodesWithPalet(List<Point> paletsPosition) {
//		List<Integer> nodesWithPalet = new ArrayList<Integer>();
//		for(Point palet : paletsPosition){
//			int paletX = palet.getX();
//			int paletY = palet.getY();
//			int delta = 1000;
//			int index = -1;
//			if (!paletIsInCamp(palet)){
//				System.out.println("Palet NOT In camp :"+palet);
//				for(int i=0; i<this.nodesPosition.size(); i++){
//					Point p = this.nodesPosition.get(i);
//					int tmpDelta = Math.abs(paletX - p.getX()) + Math.abs(paletY - p.getY());
//					if (tmpDelta < delta){
//						tmpDelta = delta;
//						index = i;
//					}
//				}
//				nodesWithPalet.add(index);
//			} else {
//				System.out.println("Palet In camp :"+palet);
//			}
//		}
//		return nodesWithPalet;
//	}
	
	private boolean paletIsInCamp(Point palet) {
		return equationsLinesColors.get(0).y == Color.WHITE && equationsLinesColors.get(1).y == Color.WHITE && (!equationsLinesColors.get(0).x.pointIsAbove(palet) || equationsLinesColors.get(1).x.pointIsAbove(palet));
	}

	private List<Integer> getNodesWithPaletCloseFromRobot(List<Integer> nodesWithPalet, int nbNodes) {
		List<Integer> nodesWithPaletCloseFromRobot = new ArrayList<Integer>();
		Integer[] deltaForClosestNodes = new Integer[nodesWithPalet.size()];
		int robotX = this.robotPosition.getX();
		int robotY = this.robotPosition.getY();
		for(int i=0; i<nodesWithPalet.size(); i++){
			Point p = this.nodesPosition.get(nodesWithPalet.get(i));
			deltaForClosestNodes[i] = Math.abs(robotX - p.getX()) + Math.abs(robotY - p.getY());
		}
		ArrayIndexComparator<Integer> comparator = new ArrayIndexComparator<Integer>(deltaForClosestNodes);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		for(int i=0; i<nbNodes; i++){
			nodesWithPaletCloseFromRobot.add(indexes[i]);
		}
		return nodesWithPaletCloseFromRobot;
	}

	/**
	 * Charge la calibration du fichier de configuration si elle existe
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void loadCalibration() throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File("calibration");
		if(file.exists()){
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			color.setCalibration((float[][])ois.readObject());
			graber.setOpenTime((long)ois.readObject());
			equationsLinesColors = (ArrayList<Tuple<EquationLine,Integer>>)ois.readObject();
			nodesPosition = (ArrayList<Point>)ois.readObject();
			ois.close();
		}
	}

	/**
	 * Sauvegarde la calibration
	 * @throws IOException
	 */
	private void saveCalibration() throws IOException {
		screen.drawText("Sauvegarde", 
				"Appuyez sur le bouton central ","pour valider id",
				"Echap pour ne pas sauver");
		if(input.waitOkEscape(Button.ID_ENTER)){
			File file = new File("calibration");
			if(!file.exists()){
				file.createNewFile();
			}else{
				file.delete();
				file.createNewFile();
			}
			ObjectOutputStream str = new ObjectOutputStream(new FileOutputStream(file));
			str.writeObject(color.getCalibration());
			str.writeObject(graber.getOpenTime());
			str.writeObject(equationsLinesColors);
			str.writeObject(nodesPosition);
			str.flush();
			str.close();
		}
	}

	/**
	 * Effectue l'ensemble des actions nécessaires à l'extinction du programme
	 */
	private void cleanUp() {
		if(!graber.isOpen()){
			graber.open();
			while(graber.isRunning()){
				graber.checkState();
			}
		}
		propulsion.runFor(500, true);
		while(propulsion.isRunning()){
			propulsion.checkState();
		}
		color.lightOff();
	}

	/**
	 * S'occupe d'effectuer l'ensemble des calibrations nécessaires au bon
	 * fonctionnement du robot.
	 * 
	 * @return vrai si tout c'est bien passé.
	 */
	private void calibration() {
		calibrationGrabber();
		calibrationCouleur();
		calibrateNodeEquationLine();
		calibrateNodePosition();
		calibrateRobotPositionAndVector();
	}

	private boolean calibrationGrabber() {
		screen.drawText("Calibration", 
						"Calibration de la fermeture de la pince",
						"Appuyez sur le bouton central ","pour continuer");
		if(input.waitOkEscape(Button.ID_ENTER)){
			screen.drawText("Calibration", 
						"Appuyez sur ok","pour lancer et arrêter");
			input.waitAny();
			graber.startCalibrate(false);
			input.waitAny();
			graber.stopCalibrate(false);
			screen.drawText("Calibration", 
						"Appuyer sur Entree", "pour commencer la",
						"calibration de l'ouverture");
			input.waitAny();
			screen.drawText("Calibration", 
						"Appuyer sur Entree", "Quand la pince est ouverte");
			graber.startCalibrate(true);
			input.waitAny();
			graber.stopCalibrate(true);

		}else{
			return false;
		}
		return true;
	}

	/**
	 * Effectue la calibration de la couleur
	 * @return renvoie vrai si tout c'est bien passé
	 */
	private boolean calibrationCouleur() {
		screen.drawText("Calibration", 
						"Préparez le robot à la ","calibration des couleurs",
						"Appuyez sur le bouton central ","pour continuer");
		if(input.waitOkEscape(Button.ID_ENTER)){
			color.lightOn();

			//calibration gris
			screen.drawText("Gris", 
					"Placer le robot sur ","la couleur grise");
			input.waitAny();
			color.calibrateColor(Color.GRAY);

			//calibration rouge
			screen.drawText("Rouge", "Placer le robot ","sur la couleur rouge");
			input.waitAny();
			color.calibrateColor(Color.RED);

			//calibration noir
			screen.drawText("Noir", "Placer le robot ","sur la couleur noir");
			input.waitAny();
			color.calibrateColor(Color.BLACK);

			//calibration jaune
			screen.drawText("Jaune", 
					"Placer le robot sur ","la couleur jaune");
			input.waitAny();
			color.calibrateColor(Color.YELLOW);

			//calibration bleue
			screen.drawText("BLeue", 
					"Placer le robot sur ","la couleur bleue");
			input.waitAny();
			color.calibrateColor(Color.BLUE);

			//calibration vert
			screen.drawText("Vert", "Placer le robot ","sur la couleur vert");
			input.waitAny();
			color.calibrateColor(Color.GREEN);

			//calibration blanc
			screen.drawText("Blanc", "Placer le robot ","sur la couleur blanc");
			input.waitAny();
			color.calibrateColor(Color.WHITE);

			color.lightOff();
			return true;
		}
		return false;
	}
}
//////////////////////////// CODE MORT ////////////////////////////////////////////////

//	public static double angleBetweenPoints(Point a, Point b) {
//        double angleA = angleFromOriginCounterClockwise(a);
//        double angleB = angleFromOriginCounterClockwise(b);
//        return Math.abs(angleA-angleB);
//    }
//
//    public static double angleFromOriginCounterClockwise(Point a) {
//        double degrees = Math.toDegrees(Math.atan(a.getY()/a.getX()));
//        if(a.getX() < 0.0) return degrees+180.0;
//        else if(a.getY() < 0.0) return degrees+360.0;
//        else return degrees;
//    }
//
//    public static void main(String[] args) {
//        Point p1 = new Point(1, 100);
//        Point p2 = new Point(-100, 1);
//        System.out.println(angleBetweenPoints(p1, p2));
//    }



//	private double angleCalculation(Point paletToGet) {
//		Point northPoint = new Point(this.robotPosition.getX(), paletToGet.getY());
//		double disRobotToNorth = Math.sqrt(Math.pow((northPoint.getX() - this.robotPosition.getX()), 2) + Math.pow((northPoint.getY() - this.robotPosition.getY()), 2));
//		double disRobotToPalet = Math.sqrt(Math.pow((paletToGet.getX() - this.robotPosition.getX()), 2) + Math.pow((paletToGet.getY() - this.robotPosition.getY()), 2));
//		return Math.toDegrees(Math.acos((disRobotToNorth/disRobotToPalet)));
//	}
