

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import ServerSearch.Point;
import ServerSearch.Server;
import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.hsp.HSP;
import fr.uga.pddl4j.util.MemoryAgent;
import linker.ParserPDDL4J;

public class TestPDDL4J {

	
	public static void main(String [] args) throws IOException {
		
		ParserPDDL4J parser = new ParserPDDL4J();
		
		List<Integer> points= Arrays.asList(3, 4, 5, 6, 7);
		
		
		parser.parse(points, 'A', true);
		
		parser.runProblem();
	
	}
	
	
	
	final static String domain = "(define (domain ANOI)\n" + 
			"	(:requirements :strips :typing)\n" + 
			"	(:types tower rondelle)\n" + 
			"	(:predicates \n" + 
			"		(ontower ?x - rondelle ?t - tower)\n" + 
			"		(on ?x - rondelle ?y - rondelle)\n" + 
			"		(clearAbove ?x - rondelle)\n" + 
			"		(clearUnder ?x - rondelle)\n" + 
			"		(towerclear ?t - tower)\n" + 
			"	)\n" + 
			"	(:action move\n" + 
			"		:parameters (?x - rondelle ?t1 - tower ?y - rondelle ?t2 - tower ?z - rondelle)\n" + 
			"		:precondition (and (clearAbove ?x) (on ?x ?y) (ontower ?x ?t1) (clearAbove ?z) (ontower ?z ?t2))\n" + 
			"		:effect (and \n" + 
			"			(not (ontower ?x ?t1))\n" + 
			"			(not (on ?x ?y))\n" + 
			"			(not (clearAbove ?z))\n" + 
			"			(ontower ?x ?t2)\n" + 
			"			(on ?x ?z)\n" + 
			"			(clearAbove ?y)\n" + 
			"		)\n" + 
			"	)\n" + 
			"	(:action moveOnClearTower\n" + 
			"		:parameters (?x - rondelle ?t1 - tower ?y - rondelle ?t2 - tower)\n" + 
			"		:precondition (and (clearAbove ?x) (on ?x ?y) (ontower ?x ?t1) (towerclear ?t2))\n" + 
			"		:effect (and \n" + 
			"			(not (ontower ?x ?t1))\n" + 
			"			(not (on ?x ?y))\n" + 
			"			(not (towerclear ?t2))\n" + 
			"			(ontower ?x ?t2)\n" + 
			"			(clearUnder ?x)\n" + 
			"			(clearAbove ?y)\n" + 
			"		)\n" + 
			"	)\n" + 
			"	(:action moveFromClearTower\n" + 
			"		:parameters (?x - rondelle ?t1 - tower ?t2 - tower ?z - rondelle)\n" + 
			"		:precondition (and (clearAbove ?x) (clearUnder ?x) (ontower ?x ?t1) (clearAbove ?z) (ontower ?z ?t2))\n" + 
			"		:effect (and \n" + 
			"			(not (ontower ?x ?t1))\n" + 
			"			(not (clearUnder ?x))\n" + 
			"			(not (clearAbove ?z))\n" + 
			"			(ontower ?x ?t2)\n" + 
			"			(towerclear ?t1)\n" + 
			"			(on ?x ?z)\n" + 
			"		)\n" + 
			"	)\n" + 
			"	(:action moveFromClearTowerOnClearTower\n" + 
			"		:parameters (?x - rondelle ?t1 - tower ?t2 - tower)\n" + 
			"		:precondition (and (clearAbove ?x) (clearUnder ?x) (ontower ?x ?t1) (towerclear ?t2))\n" + 
			"		:effect (and \n" + 
			"			(not (ontower ?x ?t1))\n" + 
			"			(not (towerclear ?t2))\n" + 
			"			(ontower ?x ?t2)\n" + 
			"			(towerclear ?t1)\n" + 
			"		)\n" + 
			"	)\n" + 
			")";
	
	final static String problem = "(define (problem ANOI)\n" + 
			"	(:domain ANOI)\n" + 
			"	(:objects G M D - tower A B C - rondelle)\n" + 
			"	(:INIT \n" + 
			"		(ontower A G)\n" + 
			"		(ontower B G)\n" + 
			"		(ontower C G)\n" + 
			"		(on B A)\n" + 
			"		(on C B)\n" + 
			"		(clearUnder A)\n" + 
			"		(clearAbove C)\n" + 
			"		(towerclear M)\n" + 
			"		(towerclear D)\n" + 
			"	)\n" + 
			"	(:goal (AND (ontower A D) (on B A) (on C B)))\n" + 
			")";
	
	
	
	
	private static String problemDefine = "(define (problem ROBOTPARTY) (:domain KROBOT)\n";
	private static String problemInit = "		(connected A B)\n" + 
			"		(connected A D)\n" + 
			"		(connected B C)\n" + 
			"		(connected B E)\n" + 
			"		(connected C F)\n" + 
			"		(connected D E)\n" + 
			"		(connected D G)\n" + 
			"		(connected E F)\n" + 
			"		(connected E H)\n" + 
			"		(connected F I)\n" + 
			"		(connected G H)\n" + 
			"		(connected G J)\n" + 
			"		(connected H I)\n" + 
			"		(connected H K)\n" + 
			"		(connected I L)\n" +
			"		(connected J K)\n" +
			"		(connected K L)\n" +
			"		(nodeInCamp A)\n" + 
			"		(nodeInCamp B)\n" +
			"		(nodeInCamp C)\n" +
			"	)\n" +
			"	(:goal\n";
	
	
    private static String domainString = "(define (domain KROBOT)\n" + 
    		"	(:requirements :strips :typing)\n" + 
    		"	(:types node palet)\n" + 
    		"	(:predicates (connected ?n ?m - node)\n" + 
    		"		(paletOnNode ?p - palet ?n - node)\n" + 
    		"		(robotOnNode ?n - node)\n" + 
    		"		(paletOnRobot ?p - palet)\n" + 
    		"		(paletInCamp ?p - palet)\n" + 
    		"		(nodeInCamp ?n - node)\n" + 
    		"		(hasPaletInCamp)\n" + 
    		"		(robotFree)\n" + 
    		"	)\n" + 
    		"\n" + 
    		"	(:action moveRobot1\n" + 
    		"		:parameters (?n ?m - node)\n" + 
    		"		:precondition (and (robotFree) (robotOnNode ?n) (connected ?n ?m))\n" + 
    		"		:effect (and\n" + 
    		"			(not (robotOnNode ?n))\n" + 
    		"			(robotOnNode ?m)\n" + 
    		"		)\n" + 
    		"	)\n" + 
    		"	(:action movePaletAndRobot1\n" + 
    		"		:parameters (?n ?m - node ?p - palet)\n" + 
    		"		:precondition (and (robotOnNode ?n) (paletOnNode ?p ?n) (connected ?n ?m) (paletOnRobot ?p))\n" + 
    		"		:effect (and\n" + 
    		"			(not (robotOnNode ?n))\n" + 
    		"			(robotOnNode ?m)\n" + 
    		"			(not (paletOnNode ?p ?n))\n" + 
    		"			(paletOnNode ?p ?m)\n" + 
    		"		)\n" + 
    		"	)\n" + 
    		"	(:action moveRobot2\n" + 
    		"		:parameters (?n ?m - node)\n" + 
    		"		:precondition (and (robotFree) (robotOnNode ?n) (connected ?m ?n))\n" + 
    		"		:effect (and\n" + 
    		"			(not (robotOnNode ?n))\n" + 
    		"			(robotOnNode ?m)\n" + 
    		"		)\n" + 
    		"	)\n" + 
    		"	(:action movePaletAndRobot2\n" + 
    		"		:parameters (?n ?m - node ?p - palet)\n" + 
    		"		:precondition (and (robotOnNode ?n) (paletOnNode ?p ?n) (connected ?m ?n) (paletOnRobot ?p))\n" + 
    		"		:effect (and\n" + 
    		"			(not (robotOnNode ?n))\n" + 
    		"			(robotOnNode ?m)\n" + 
    		"			(not (paletOnNode ?p ?n))\n" + 
    		"			(paletOnNode ?p ?m)\n" + 
    		"		)\n" + 
    		"	)\n" + 
    		"	(:action getPalet\n" + 
    		"		:parameters (?n - node ?p - palet)\n" + 
    		"		:precondition (and (robotOnNode ?n) (paletOnNode ?p ?n) (robotFree))\n" + 
    		"		:effect (and\n" + 
    		"			(paletOnRobot ?p)\n" + 
    		"			(not (robotFree))\n" + 
    		"		)\n" + 
    		"	)\n" + 
    		"	(:action releasePalet\n" + 
    		"		:parameters (?n - node ?p - palet)\n" + 
    		"		:precondition (and (robotOnNode ?n) (paletOnRobot ?p) (nodeInCamp ?n))\n" + 
    		"		:effect (and\n" + 
    		"			(not (paletOnRobot ?p))\n" + 
    		"			(robotFree)\n" + 
    		"			(paletInCamp ?p)\n" + 
    		"			(hasPaletInCamp)\n" + 
    		"		)\n" + 
    		"	)\n" + 
    		")";
}
