

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.parser.Parser;
import fr.uga.pddl4j.planners.hsp.HSP;
import fr.uga.pddl4j.util.MemoryAgent;

public class TestPDDL4J {

	
	public static void main(String [] args) throws IOException {
		
		
		File tempDomain = new File("domain.pddl");

        /* On rempli ce fichier temporaire */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempDomain))) {
            writer.write(domain);
        }
        
        File tempProblem = new File("problem.pddl");

        /* On rempli ce fichier temporaire */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempProblem))) {
            writer.write(problem);
        }
		
		String [] argumentsString = {"-o", "domain.pddl", "-f", "problem.pddl" };
        
		
		final Properties arguments = HSP.parseArguments(argumentsString);
        // Create the planner
        HSP planner = new HSP(arguments);
        // Parse and encode the PDDL file into compact representation
        final CodedProblem problem = planner.parseAndEncode();
        // Search for a solution and print the result
        planner.search(problem);
		
		
		System.out.println("Done!");
		
//        planner.search(cp);

//        while(true);
	
	
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
	
}
