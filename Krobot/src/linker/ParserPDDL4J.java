package linker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.hsp.HSP;

public class ParserPDDL4J {
	Parser parser = new Parser();
	String problem;
	
	public ParserPDDL4J() {

	}
	
	
	public void parse(List<Integer> nodesWithPalet, char nodeRobot, boolean robotFree){
		problem = problemDefine;
		problem += "	(:objects A B C D E F G H I J K L - node";
		for (int i=0; i<nodesWithPalet.size(); i++)
			problem += " P" + i;
		problem += " - palet)\n";
		problem += "	(:init\n";
		if (robotFree) 
			problem += "		(robotFree)\n";
		for (int i=0; i<nodesWithPalet.size(); i++){
			problem += "		(paletOnNode P" + i + " " + (char) (nodesWithPalet.get(i)+65) + ")\n";
		}
		problem += "		(robotOnNode " + nodeRobot + ")\n";
		problem += problemInit;
		if (nodesWithPalet.size() > 1){
			problem += "		(and\n";
			for(int i=0; i<nodesWithPalet.size(); i++){
				problem += "			(paletInCamp P" + i + ")\n";
			}
			problem += "		)\n";
		} else {
			problem += "		(paletInCamp 0)\n";
		}
		problem += 	"	)\n" + 
					")";
		System.out.println(problem);
		
		
//		parser.parseStringProblem(problem);
	}
	
	public void runProblem() throws IOException{
		

		
		File tempDomain = new File("domain.pddl");

        /* On rempli ce fichier temporaire */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempDomain))) {
            writer.write(domainString);
        }
        
        File tempProblem = new File("problem.pddl");

        /* On rempli ce fichier temporaire */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempProblem))) {
            writer.write(problem);
        }
		
		String [] argumentsString = {"-o", "domain.pddl", "-f", "problem.pddl" };
        
		final Properties arguments = HSP.parseArguments(argumentsString);
		
		HSP planner = new HSP(arguments);
		
	
		final CodedProblem Cbproblem = planner.parseAndEncode();
        
        // Search for a solution and print the result
        List<String> plan = planner.search(Cbproblem);
		
        if(plan != null) {
        	
        	System.out.println("Plan:");
    		for(String s: plan) {
    			System.out.println(s);
    		}
        }
		
		
		
		
	}
	
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
