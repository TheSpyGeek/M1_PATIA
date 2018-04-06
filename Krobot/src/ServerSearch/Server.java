package ServerSearch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread gérant la reception des position des item du terrain de la caméra.
 * Créer une liste de point qui sera traitée par EyeOfMarvin.
 * @see Item
 * @author paul.carretero, florent.chastagner
 */
public class Server {
	
	/**
	 * port sur lequel recevoir les positions des item du terrain fournies par la caméra
	 */
	private static final int PORT = 8888;
	
	/**
	 * buffer pour la reception des données
	 */
	private byte[] buffer = new byte[2048];
	
	/**
	 * temps en ms de la dernière reception des positions
	 */
	//private int lastReceivedTimer = 0;
	
	/**
	 * vrai si le Thread doit se terminer, faux sinon
	 */
	//private volatile boolean stop = false;
	
	/**
	 * Socket du serveur
	 */
	private DatagramSocket dsocket;
	
	/**
	 * paquet UDP reçu contenant les positions des items
	 */
	private DatagramPacket packet;
	
	/**
	 * Liste d'item contenant les points (bruts) reçu de la caméra.
	 */
	//private ArrayList<Point> palets;
	
	/**
	 * déplacement sur l'axe des X a appliquer aux données reçues
	 */
	//private static int xOffset = 0;
	
	/**
	 * déplacement sur l'axe des Y a appliquer aux données reçues
	 */
	//private static int yOffset = 0;

	/**
	 * @param sl un objet (EyeOfMarvin dans ce cas) permettant de traiter la reception de la liste de points.
	 */
	public Server(){
		this.packet = new DatagramPacket(this.buffer, this.buffer.length);
		
		
	}
	
	/**
	 * @param x déplacement sur l'axe des X a appliquer aux données reçues
	 * @param y déplacement sur l'axe des Y a appliquer aux données reçues
	 */
//	public static void defineOffset(int x, int y){
//		xOffset = x;
//		yOffset = y;
//	}
	
	/**
	 * Récupère les positions des items fournies par la caméra, les encode en Item et transmet cette liste a EyeOfMarvin pour traitement
	 */
	
	public List<Point> run() {
		try {
			this.dsocket = new DatagramSocket(PORT);
		} catch (SocketException e1) {
			System.out.println(": Erreur, DatagramSocket non initialisé");
			e1.printStackTrace();
		}
		/* on recupère le packet avec les coordonnées */
		try {
			this.dsocket.receive(this.packet);
		} catch (IOException e) {
			System.out.println("Closed");
			//this.stop = true;
		}
		
		/* on la parse */
		String msg = new String(this.buffer, 0, this.packet.getLength());
		String[] items = msg.split("\n");
		List<Point> points = new ArrayList<Point>();
		
		/* on parcours ce parsing pour ajouter dans notre tableau */
		for (int i = 0; i < items.length; i++) {
			String[] coord = items[i].split(";");
			if(coord.length == 3){
	        	int x = Integer.parseInt(coord[1]);
	        	int y = 300 - Integer.parseInt(coord[2]); // convertion en mode 'genius'
	        	Point p = new Point(x, y);
	        	points.add(p);
			}
        }
		interrupt();
		return points;
	}
	
	
	public void interrupt(){
		this.dsocket.close();
		//this.stop = true;
	}
	
	public static void main(String[] args) {
		Server s = new Server();
		List<Point> points = s.run();
		Collections.sort(points);
		System.out.println(points);
	}

}
      