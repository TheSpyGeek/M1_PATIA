(define (domain KROBOT)
	(:requirements :strips :typing)
	(:types node palet)
	(:predicates (connected ?n ?m - node)
		(paletOnNode ?p - palet ?n - node)
		(robotOnNode ?n - node)
		(paletOnRobot ?p - palet)
		(paletInCamp ?p)
		(nodeInCamp ?n)
		(robotFree)
	)

	(:action moveRobot1
		:parameters (?n ?m - node)
		:precondition (and (robotFree) (robotOnNode ?n) (connected ?n ?m))
		:effect (and
			(not (robotOnNode ?n))
			(robotOnNode ?m)
		)
	)
	(:action movePaletAndRobot1
		:parameters (?n ?m - node ?p - palet)
		:precondition (and (robotOnNode ?n) (paletOnNode ?p ?n) (connected ?n ?m) (paletOnRobot ?p))
		:effect (and
			(not (robotOnNode ?n))
			(robotOnNode ?m)
			(not (paletOnNode ?p ?n))
			(paletOnNode ?p ?m)
		)
	)
	(:action moveRobot2
		:parameters (?n ?m - node)
		:precondition (and (robotFree) (robotOnNode ?n) (connected ?m ?n))
		:effect (and
			(not (robotOnNode ?n))
			(robotOnNode ?m)
		)
	)
	(:action movePaletAndRobot2
		:parameters (?n ?m - node ?p - palet)
		:precondition (and (robotOnNode ?n) (paletOnNode ?p ?n) (connected ?m ?n) (paletOnRobot ?p))
		:effect (and
			(not (robotOnNode ?n))
			(robotOnNode ?m)
			(not (paletOnNode ?p ?n))
			(paletOnNode ?p ?m)
		)
	)
	(:action getPalet
		:parameters (?n - node ?p - palet)
		:precondition (and (robotOnNode ?n) (paletOnNode ?p ?n) (robotFree))
		:effect (and
			(paletOnRobot ?p)
			(not (robotFree))
		)
	)
	(:action releasePalet
		:parameters (?n - node ?p - palet)
		:precondition (and (robotOnNode ?n) (paletOnRobot ?p) (nodeInCamp ?n))
		:effect (and
			(not (paletOnRobot ?p))
			(robotFree)
			(paletInCamp ?p)
		)
	)
)
