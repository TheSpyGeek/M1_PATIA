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
import utils.R2D2Constants;
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
	protected boolean		 top 		= false;
	
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
		screen.drawText("Calibration", 
				"Appuyez sur echap ","pour skipper");
		boolean skip = input.waitOkEscape(Button.ID_ESCAPE);
		if(skip || calibration()){
			if(!skip){
				saveCalibration();
			}
			screen.drawText("Lancer", 
				"Appuyez sur OK si la","ligne noire est à gauche",
				"Appuyez sur tout autre", "elle est à droite");
			calibrateNodePosition();
			screen.drawText("Lancer", 
					"Mettre un palet sur","le robot","OK si haut autre", "si bas");
			if(input.isThisButtonPressed(input.waitAny(), Button.ID_ENTER)){
				this.top = true;
				calibrateRobotPositionAndVector();
			}else{
				this.top = false;
				calibrateRobotPositionAndVector();
			}
			screen.drawText("Lancer", 
					"Ok pour run");
			if(input.isThisButtonPressed(input.waitAny(), Button.ID_ENTER)){
				runIA();
			}
//			if(input.isThisButtonPressed(input.waitAny(), Button.ID_ENTER)){
//				mainLoop(true);
//			}else{
//				mainLoop(false);
//			}
		}
		cleanUp();
	}

	private void calibrateNodePosition() {
		this.server = new Server();
		List<Point> tmp = server.run();
		if (tmp.size() != 9){
			System.out.println("Error bad number of palet on table");
			return;
		}
		Collections.sort(tmp);
		System.out.println("Point triées :");
		for(Point p : tmp)
			System.out.println(p);
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
		System.out.println("Nodes : ");
		for(Point node : this.nodesPosition)
			System.out.println(node);
	}
	
	private void calibrateRobotPositionAndVector() {
		this.server = new Server();
		List<Point> tmp = server.run();
		Collections.sort(tmp);
		System.out.println("Sorted palet :");
		this.robotPosition = tmp.get(0);
		for (Point p : tmp) {
			if(top && this.robotPosition.getY() < p.getY()) {
				this.robotPosition = p;
			} else if(!top && this.robotPosition.getY() > p.getY()) {
				this.robotPosition = p;
			}
			System.out.println(p);
		}
			
		//this.robotPosition = tmp.get(top ? 0 : tmp.size()-1);
		System.out.println("RobotPosition : "+this.robotPosition);
		Point far;
		Point far1 = nodesPosition.get(2);
		Point far2 = nodesPosition.get(5);
		Point far3 = nodesPosition.get(8);
		if (top){
			far1 = nodesPosition.get(0);
			far2 = nodesPosition.get(3);
			far3 = nodesPosition.get(6);
		}
		System.out.println(far1 + " " + far2 + " " + far3);
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
		System.out.println("Far point : "+far );
		robotVecteur = new Point(far.getX() - this.robotPosition.getX(), far.getY() - this.robotPosition.getY());
		System.out.println("VecteurRobot="+robotVecteur);
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
	
	/*private void runIAPDDL() {
		
		List<String> moveToDo;
		List<Integer> nodesWithPalet = getNodesWithPalet(server.run());
		while (!nodesWithPalet.isEmpty()){
			
			// calcul node robot
			char nodeRobot = '0';
			parser.parse(nodesWithPalet, nodeRobot, true);
			try {
				moveToDo = parser.runProblem();
				
				
			} catch (IOException e) {
				System.err.println("Erreur lors du run problem");
				e.printStackTrace();
			}
			
			
			nodesWithPalet = getNodesWithPalet(server.run());
		}
		
	}*/

	private void runIA() {
		List<Integer> nodesWithPalet = getNodesWithPalet(server.run());
		while (!nodesWithPalet.isEmpty()){
			List<Integer> nodesWithPaletCloseFromRobot = getNodesWithPaletCloseFromRobot(nodesWithPalet, 5);
			parser.parse(nodesWithPaletCloseFromRobot, getNodeWithRobot(), true);
			
			//On récupère les actions à effectué !
			Point paletToGet = nodesPosition.get(nodesWithPalet.get(0));
			System.out.println("Palet to get : "+paletToGet);
			Point vRobPal = new Point(paletToGet.getX() - this.robotPosition.getX(), paletToGet.getY() - this.robotPosition.getY());
			double angleToRotate = angleCalculation(paletToGet);
			double zproduct = (vRobPal.getX() * robotVecteur.getY()) - (vRobPal.getY() * robotVecteur.getX());
			double dotProd = (vRobPal.getX() * robotVecteur.getX()) + (vRobPal.getY() * robotVecteur.getY());
			boolean turnLeft = (zproduct * dotProd) < 0;
			System.out.println("zprod = "+zproduct + " dotProd = "+dotProd + " turnLeft = "+turnLeft);
			angleToRotate = Math.abs(angleToRotate);
			System.out.println("Turn Left = "+turnLeft);
			
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
			
			robotPosition = paletToGet;
			robotVecteur = vRobPal;
			
			Point toHome = new Point(robotPosition.getX(), top ? robotPosition.getY() - 20 : robotPosition.getY() + 20);
			System.out.println("Palet to get : "+paletToGet);
			Point vRobHome = new Point(toHome.getX() - this.robotPosition.getX(), toHome.getY() - this.robotPosition.getY());
			angleToRotate = angleCalculation(toHome);
			zproduct = (vRobHome.getX() * robotVecteur.getY()) - (vRobHome.getY() * robotVecteur.getX());
			dotProd = (vRobHome.getX() * robotVecteur.getX()) + (vRobHome.getY() * robotVecteur.getY());
			turnLeft = (zproduct * dotProd) < 0;
			System.out.println("zprod = "+zproduct + " dotProd = "+dotProd + " turnLeft = "+turnLeft);
			angleToRotate = Math.abs(angleToRotate);
			System.out.println("Turn Left = "+turnLeft);
			
			propulsion.rotate((float)angleToRotate, turnLeft, false);
			while(propulsion.isRunning()){
				propulsion.checkState();
				if(input.escapePressed())
					return;
			}
			propulsion.run(true);
			while(propulsion.isRunning() && color.getCurrentColor() != Color.WHITE){
				propulsion.checkState();
				if(input.escapePressed())
					return;
			}
			propulsion.stopMoving();
			graber.open();
			while (graber.isRunning()){
				graber.checkState();
			}
			propulsion.runFor(500, false);
			nodesWithPalet = getNodesWithPalet(server.run());
		}
	}

	private double angleCalculation(Point paletToGet) {
		System.out.println("PaletToGet" + paletToGet);
		Point vRobPal = new Point(paletToGet.getX() - this.robotPosition.getX(), paletToGet.getY() - this.robotPosition.getY());
		double normRabPal = Math.sqrt(Math.pow(vRobPal.getX(), 2) + Math.pow(vRobPal.getY(), 2));
		double normRobVec = Math.sqrt(Math.pow(robotVecteur.getX(), 2) + Math.pow(robotVecteur.getY(), 2));
		double degree = Math.toDegrees(Math.acos(((vRobPal.getX()*robotVecteur.getX()) + (vRobPal.getY()*robotVecteur.getY())) / (normRabPal * normRobVec)));
		if ((robotVecteur.getX()*-vRobPal.getY()) - robotVecteur.getY()*vRobPal.getX() < 0){
			degree = - degree;
		}
		System.out.println("Degree to turn = "+degree);
		return degree;
	}
	
//	private double angleCalculation(Point paletToGet) {
//		Point northPoint = new Point(this.robotPosition.getX(), paletToGet.getY());
//		double disRobotToNorth = Math.sqrt(Math.pow((northPoint.getX() - this.robotPosition.getX()), 2) + Math.pow((northPoint.getY() - this.robotPosition.getY()), 2));
//		double disRobotToPalet = Math.sqrt(Math.pow((paletToGet.getX() - this.robotPosition.getX()), 2) + Math.pow((paletToGet.getY() - this.robotPosition.getY()), 2));
//		return Math.toDegrees(Math.acos((disRobotToNorth/disRobotToPalet)));
//	}

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

	private List<Integer> getNodesWithPalet(List<Point> paletsPosition) {
		List<Integer> nodesWithPalet = new ArrayList<Integer>();
		for(Point palet : paletsPosition){
			int paletX = palet.getX();
			int paletY = palet.getY();
			int delta = 1000;
			int index = -1;
			if (paletX > R2D2Constants.XPOSITION_LINE_CAMP_1 && paletX < R2D2Constants.XPOSITION_LINE_CAMP_2){
				for(int i=0; i<this.nodesPosition.size(); i++){
					Point p = this.nodesPosition.get(i);
					int tmpDelta = Math.abs(paletX - p.getX()) + Math.abs(paletY - p.getY());
					if (tmpDelta < delta){
						tmpDelta = delta;
						index = i;
					}
				}
				nodesWithPalet.add(index);
			}
		}
		return nodesWithPalet;
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
	 * Lance les tests du robot, peut être desactivé pour la persy cup
	 */
	private void runTests() {
//		SystemTest.grabberTest(this);
	}

	/**
	 * Lance la boucle de jeu principale
	 * 
	 * Toutes les opérations dans la boucle principale doivent être le plus
	 * atomique possible.
	 * Cette boucle doit s'executer très rapidement.
	 */
	enum States {
		firstMove,
		step2,
		step22,
		playStart,
		isCatching,
		needToRelease,
		isReleasing,
		needToSeek,
		isSeeking,
		needToGrab,
		isGrabing,
		needToRotateEast,
		isRotatingToEast,
		needToRotateWest,
		isRotatingToWest,
		needToGoBackHome,
		isRunningBackHome,
		needToResetInitialSeekOrientation,
		isResetingInitialSeekOrientation,
		needToTurnBackToGoBackHome,
		isTurningBackToGoBackHome,
		needToOrientateNorthToRelease,
		isOrientatingNorthToRealease,
		isAjustingBackHome,
		isGoingToOrientateN}
	private void mainLoop(boolean initLeft) {
		States state          = States.firstMove;
		boolean run           = true;
		boolean unique        = true;
		boolean unique2       = true;
		float   searchPik     = R2D2Constants.INIT_SEARCH_PIK_VALUE;
		boolean isAtWhiteLine = false;
		int     nbSeek        = R2D2Constants.INIT_NB_SEEK;
		boolean seekLeft      = initLeft;
		//Boucle de jeu
		while(run){
			/*
			 * - Quand on part chercher un palet, on mesure le temps de trajet
			 * - Quand on fait le demi tour on parcours ce même temps de trajet
			 * - Si on croise une ligne noire vers la fin du temps de trajet
			 *     S'orienter au nord
			 *     vérifier pendant l'orientation la présence d'une ligne blanche
			 *     si on voit une ligne blanche alors le prochain état sera 
			 *     arrivé à la maison
			 *     sinon le prochain état sera aller à la maison.
			 */
			try{
				for(TImedMotor m : motors){
					m.checkState();
				}
				switch (state) {
				/*
				 * Routine de démarrage du robot :
				 *    Attraper un palet
				 *    Emmener le palet dans le but adverse les roues à cheval
				 *    sur la ligne noire.
				 *    Et passer dans l'état needToResetInitialSeekOrientation
				 */
				case firstMove :
					propulsion.run(true);
					state = States.playStart;
					break;
				case playStart:
					while(propulsion.isRunning()){
						if(pression.isPressed()){
							propulsion.stopMoving();
							graber.close();
						}
					}
					propulsion.rotate(R2D2Constants.ANGLE_START, seekLeft, false);
					while(propulsion.isRunning() || graber.isRunning()){
						propulsion.checkState();
						graber.checkState();
						if(input.escapePressed())
							return;
					}
					propulsion.run(true);
					while(propulsion.isRunning()){
						propulsion.checkState();
						if(input.escapePressed())
							return;
						if(color.getCurrentColor() == Color.WHITE){
							propulsion.stopMoving();
						}
					}
					graber.open();
					while(graber.isRunning()){
						graber.checkState();
						if(input.escapePressed())
							return;
					}
					propulsion.runFor(R2D2Constants.QUARTER_SECOND, false);
					while(propulsion.isRunning()){
						propulsion.checkState();
						if(input.escapePressed())
							return;
					}
					propulsion.halfTurn(seekLeft);
					while(propulsion.isRunning()){
						propulsion.checkState();
						if(input.escapePressed())
							return;
					}
					propulsion.run(true);
					while(propulsion.isRunning()){
						propulsion.checkState();
						if(input.escapePressed())
							return;
						if(color.getCurrentColor() == Color.BLACK){
							propulsion.stopMoving();
						}
					}
					/*
					propulsion.orientateSouth(seekLeft);
					while(propulsion.isRunning()){
						propulsion.checkState();
						if(input.escapePressed())
							return;
					}
					state = States.needToGrab;
					*/
					state = States.needToSeek;
				break;
				/*
				 * Le bsoin de chercher un objet nécessite d'avoir le robot
				 * orienté face à l'ouest du terrain. Le nord étant face au camp
				 * adverse
				 * Le robot va lancer une rotation de 180° en cherchant si un
				 * pic de distances inférieure à 70cm apparait.
				 * Dans ce cas, il fera une recherche du centre de l'objet et
				 * ira l'attraper
				 *
				 * TODO faire en sorte que le robot n'avance pas pour une durée
				 * indeterminée, mais qu'il avance sur un temps de référence
				 * pour 70 cm de trajet au maximum. Comme ça, si l'objet a été
				 * attrapé pendant ce temps ou à disparu, alors il ne roulera
				 * pas dans le vide pour rien
				 */
				case needToSeek:
					state = States.isSeeking;
					searchPik   = R2D2Constants.INIT_SEARCH_PIK_VALUE;
					propulsion.volteFace(seekLeft, R2D2Constants.SEARCH_SPEED);
					isAtWhiteLine = false;
					break;
				case isSeeking:
					float newDist = vision.getRaw()[0];
					//Si la nouvelle distance est inférieure au rayonMaximum et
					//et supérieure au rayon minimum alors
					//on a trouvé un objet à rammaser.
					if(newDist < R2D2Constants.MAX_VISION_RANGE
					   && newDist >= R2D2Constants.MIN_VISION_RANGE){
						if(searchPik == R2D2Constants.INIT_SEARCH_PIK_VALUE){
							if(unique2){
								unique2 = false;
							}else{
								propulsion.stopMoving();
								//TODO, ces 90° peuvent poser problème.
								//Genre, dans le cas où le dernier palet de la recherche
								//a déclenché la recherche du searchPik,
								//du coup on risque de voir le mur.
								//Il serait plus intéressant de faire un rotate
								//west ou east en fonction.
								//Mais bon, on a jamais eu le bug alors ...
								propulsion.rotate(R2D2Constants.QUART_CIRCLE, 
								                  seekLeft, 
								                  R2D2Constants.SLOW_SEARCH_SPEED);
								searchPik = newDist;
							}
						}else{
							if(newDist <= searchPik){
								searchPik = newDist;
							}else{
								propulsion.stopMoving();
								unique2 = true;
								state = States.needToGrab;
							}
						}
					}else{
						searchPik = R2D2Constants.INIT_SEARCH_PIK_VALUE;
					}
					if(!propulsion.isRunning() && state != States.needToGrab){
						nbSeek   += R2D2Constants.STEPS_PER_STAGE;
						if(nbSeek > 10){
							run = false;
						}
						state    = States.needToOrientateNorthToRelease;
						seekLeft = System.currentTimeMillis() % 2 == 0;
					}
					break;
				/*
				 * Le besoin d'attraper un objet correspond au besoin de rouler
				 * sur l'objet pour l'attraper dans les pinces.
				 */
				case needToGrab:
					propulsion.runFor(R2D2Constants.MAX_GRABING_TIME, true);
					state    = States.isGrabing;
					seekLeft = !seekLeft;
					break;
				/*
				 * Le robot est dans l'état isGrabing tant qu'il roule pour
				 * attraper l'objet.
				 */
				case isGrabing:
					//si le temps de roulage est dépassé, s'arrêter aussi
					if(vision.getRaw()[0] < R2D2Constants.COLLISION_DISTANCE ||
					   pression.isPressed()                                  ||
					   !propulsion.isRunning()){
						propulsion.stopMoving();
						state = States.isCatching;
						graber.close();
					}
					break;
				/*
				 * Is catching correspond à l'état où le robot est en train
				 * d'attraper l'objet.
				 * Cet état s'arrête quand les pinces arrêtent de tourner, temps
				 * fonction de la calibration
				 */
				case isCatching:
					if(!graber.isRunning()){
						state = States.needToTurnBackToGoBackHome;
					}
					break;
				/*
				 * Ce état demande au robot de rentrer avec un palet.
				 * Dans un premier temps il effectue un demi tour pour repartir
				 * sur la trajectoire d'où il viens
				 */
				case needToTurnBackToGoBackHome:
					propulsion.volteFace(true, R2D2Constants.VOLTE_FACE_ROTATION);
					state = States.isTurningBackToGoBackHome;
					break;
				case isTurningBackToGoBackHome:
					if(!propulsion.isRunning()){
						state = States.needToGoBackHome;
					}
					break;
				/*
				 * Dans un second temps, le robot va aller en ligne droite pour
				 * rentrer.
				 * Le temps de trajet aller a été mesuré. Nous utilisons cette
				 * mesure pour "prédire" à peux prêt quand est-ce que le robot
				 * va arriver à destination.
				 * Nous allumerons les capteurs de couleurs dans les environs
				 * pour détecter la présence d'une ligne blanche ou d'une ligne
				 * noire et agir en conséquence.
				 *
				 * Si une ligne noire est détectée, alors le robot va s'orienter
				 * face au nord et continuer sa route en direction du camp
				 * adverse.
				 *
				 * Celà permet d'assurer que le robot restera au centre du
				 * terrain.
				 *
				 * Si une ligne blanche est détectée, alors le robot sait qu'il
				 * est arrivé et l'état isRunningBackHome sera évacué
				 */
				case needToGoBackHome:
					propulsion.run(true);
					state = States.isRunningBackHome;
					break;
				case isRunningBackHome:
					if(!propulsion.isRunning()){
						state = States.needToOrientateNorthToRelease;
					}
					if(propulsion.hasRunXPercentOfLastRun(R2D2Constants.ACTIVATE_SENSOR_AT_PERCENT)){
						if(color.getCurrentColor() == Color.WHITE){
							propulsion.stopMoving();
							isAtWhiteLine = true;
							unique        = true;
						}
						if(unique && color.getCurrentColor() == Color.BLACK){
							propulsion.stopMoving();
							unique = false;
							state  = States.isAjustingBackHome;
						}
					}
					break;
				/*
				 * Cet état permet de remettre le robot dans la direction du
				 * nord avant de reprendre sa route
				 */
				case isAjustingBackHome:
					if(!propulsion.isRunning()){
						propulsion.orientateNorth();
						state = States.isGoingToOrientateN;
					}
					break;
				/*
				 * Cet état correspond à l'orientation du robot face au camp
				 * adverse pour continuer sa route.
				 *
				 * Il y a cependant un cas particulier, dans le cas où quand le
				 * robot tourne, si il voit la couleur blanche, c'est qu'il est
				 * arrivé. Dans ce cas, terminer la rotation dans l'état
				 * isOrientatingNorthToRealease.
				 */
				case isGoingToOrientateN:
					if(color.getCurrentColor() == Color.WHITE){
						state = States.isOrientatingNorthToRealease;
					}
					if(!propulsion.isRunning()){
						state = States.needToGoBackHome;
					}
					break;
				/*
				 * Correspond à l'état où le robot s'oriente au nord pour
				 * relâcher l'objet
				 */
				case needToOrientateNorthToRelease:
					state = States.isOrientatingNorthToRealease;
					propulsion.orientateNorth();
					break;
				case isOrientatingNorthToRealease:
					if(!propulsion.isRunning()){
						if(graber.isClose()){
							state = States.needToRelease;
						}else{
							state = States.needToResetInitialSeekOrientation;
						}
					}
					break;
				/*
				 * Ce état correspond, au moment où le robot a besoin de déposer
				 * le palet dans le cap adverse.
				 */
				case needToRelease:
					graber.open();
					state = States.isReleasing;
					break;
				case isReleasing:
					if(!graber.isRunning()){
						state = States.needToResetInitialSeekOrientation;
					}
					break;
				/*
				 * Une fois l'objet rammassé, il faut se remettre en position de
				 * trouver un autre objet.
				 * Le robot fait une marcher arrière d'un certain temps.
				 * Puis fera une mise en face de l'ouest
				 */
				case needToResetInitialSeekOrientation:
					state = States.isResetingInitialSeekOrientation;
					if(isAtWhiteLine){
						propulsion.runFor(R2D2Constants.HALF_SECOND*nbSeek, false);
					}else{
						propulsion.runFor(R2D2Constants.EMPTY_HANDED_STEP_FORWARD, false);
					}
					break;
				case isResetingInitialSeekOrientation:
					if(!propulsion.isRunning()){
						if(seekLeft){
							state = States.needToRotateWest;
						} else {
							state = States.needToRotateEast;
						}
						if(color.getCurrentColor()== Color.WHITE)//fin de partie
							return;
					}
					break;
				/*
				 * Remet le robot face à l'ouest pour recommencer la recherche.
				 * Le robot doit avoir suffisamment reculé pour être dans une
				 * zone où il y aura des palets à ramasser.
				 */
				case needToRotateWest:
					propulsion.orientateWest();
					state = States.isRotatingToWest;
					break;
				case isRotatingToWest:
					if(!propulsion.isRunning()){
						state = States.needToSeek;
					}
					break;
				/*
				 * Remet le robot face à l'est pour recommencer la recherche.
				 * Le robot doit avoir suffisamment reculé pour être dans une
				 * zone où il y aura des palets à ramasser.
				 */
				case needToRotateEast:
					propulsion.orientateEast();
					state = States.isRotatingToWest;
					break;
				case isRotatingToEast:
					if(!propulsion.isRunning()){
						state = States.needToSeek;
					}
					break;
				//Évite la boucle infinie
				}
				if(input.escapePressed())
					run = false;
			}catch(Throwable t){
				t.printStackTrace();
				run = false;
			}
		}
	}

	/**
	 * S'occupe d'effectuer l'ensemble des calibrations nécessaires au bon
	 * fonctionnement du robot.
	 * 
	 * @return vrai si tout c'est bien passé.
	 */
	private boolean calibration() {
		return calibrationGrabber() && calibrationCouleur();
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
