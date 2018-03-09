package linker;

import java.io.FileNotFoundException;
import java.util.List;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.encoding.Encoder;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.hsp.HSP;
import fr.uga.pddl4j.util.BitOp;
import fr.uga.pddl4j.util.Plan;

public class ParserPDDL4J {
	Parser parser = new Parser();
	String problem;
	
	public ParserPDDL4J() {
		try {
			parser.parseDomain(domainString);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void parse(List<String> nodesWithPalet, String nodeRobot, boolean robotFree){
		try {
			problem = problemDefine;
			problem += "	(:objects A B C D E F G H I J K L - node ";
			for (int i=0; i<nodesWithPalet.size(); i++)
				problem += i + " ";
			problem += "- palet)\n";
			problem += "	(:init\n";
			if (robotFree) 
				problem += "		(robotFree)\n";
			for (int i=0; i<nodesWithPalet.size(); i++){
				problem += "		(paletOnNode " + i + " " + nodesWithPalet.get(i) + ")\n";
			}
			problem += "		(robotOnNode " + nodeRobot + ")\n";
			problem += problemInit;
			parser.parse(problem);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void runProblem(){
		HSP planner = new HSP();
		planner.setTimeOut(10);
		planner.setTraceLevel(0);
		planner.setSaveState(false);
        CodedProblem cp = Encoder.encode(parser.getDomain(), parser.getProblem());
        Plan plan = null;
        if (cp.isSolvable()) {
            plan = planner.search(cp);
        }
        if (plan == null) { // no solution in TIMEOUT computation time
            System.out.println("No solution found in 10 seconds for the problem");
        } else if (plan.isEmpty()) { // Empty solution
            System.out.println("Empty solution for the problem");
        } else { // Save output plan
        	for(BitOp action : plan.actions()){
        		System.out.println(action.getName());
        	}
            cp.toString(plan);
        }
	}
	
	private static String problemDefine = "(define (problem ROBOTPARTY) (:domain KROBOT)\n";
	private static String problemInit = "		(connected A B)\n" + 
			"		(connected A D)\n" + 
			"		(connected B A)\n" + 
			"		(connected B C)\n" + 
			"		(connected B E)\n" + 
			"		(connected C B)\n" + 
			"		(connected C F)\n" + 
			"		(connected D A)\n" + 
			"		(connected D E)\n" + 
			"		(connected D G)\n" + 
			"		(connected E B)\n" + 
			"		(connected E D)\n" + 
			"		(connected E F)\n" + 
			"		(connected E H)\n" + 
			"		(connected F C)\n" + 
			"		(connected F E)\n" + 
			"		(connected F I)\n" + 
			"		(connected G D)\n" + 
			"		(connected G H)\n" + 
			"		(connected G J)\n" + 
			"		(connected H E)\n" + 
			"		(connected H G)\n" + 
			"		(connected H I)\n" + 
			"		(connected H K)\n" + 
			"		(connected I F)\n" + 
			"		(connected I H)\n" + 
			"		(connected I L)\n" + 
			"		(connected J G)\n" + 
			"		(connected J K)\n" + 
			"		(connected K H)\n" + 
			"		(connected K J)\n" + 
			"		(connected K L)\n" + 
			"		(connected L I)\n" + 
			"		(connected L K)\n" + 
			"		(nodeInCamp A)\n" + 
			"		(nodeInCamp B)\n" + 
			"		(nodeInCamp C)\n" + 
			"	)";
	
    private static String domainString = "(define (domain KROBOT)\n" + 
    		"	(:requirements :strips :typing)\n" + 
    		"	(:types node palet)\n" + 
    		"	(:predicates (connected ?n ?m - node)\n" + 
    		"		(paletOnNode ?p - palet ?n - node)\n" + 
    		"		(robotOnNode ?n - node)\n" + 
    		"		(paletOnRobot ?p - palet)\n" + 
    		"		(paletInCamp ?p)\n" + 
    		"		(nodeInCamp ?n)\n" + 
    		"		(hasPaletInCamp)\n" + 
    		"		(robotFree)\n" + 
    		"	)\n" + 
    		"\n" + 
    		"	(:action moveRobot\n" + 
    		"		:parameters (?n ?m - node)\n" + 
    		"		:precondition (and (robotFree) (robotOnNode ?n) (connected ?n ?m))\n" + 
    		"		:effect (and\n" + 
    		"			(not (robotOnNode ?n))\n" + 
    		"			(robotOnNode ?m)\n" + 
    		"		)\n" + 
    		"	)\n" + 
    		"	(:action movePaletAndRobot\n" + 
    		"		:parameters (?n ?m - node ?p - palet)\n" + 
    		"		:precondition (and (robotOnNode ?n) (paletOnNode ?p ?n) (connected ?n ?m) (paletOnRobot ?p))\n" + 
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
