(define (problem ROBOTPARTY) (:domain KROBOT)
	(:objects A B C D E F G H I J K L - node Y W U - palet)
	(:init
		(robotOnNode B)
		(robotFree)
		(paletOnNode Y E)
		(paletOnNode W G)
		(paletOnNode U I)
		(connected A B)
		(connected A D)
		(connected B A)
		(connected B C)
		(connected B E)
		(connected C B)
		(connected C F)
		(connected D A)
		(connected D E)
		(connected D G)
		(connected E B)
		(connected E D)
		(connected E F)
		(connected E H)
		(connected F C)
		(connected F E)
		(connected F I)
		(connected G D)
		(connected G H)
		(connected G J)
		(connected H E)
		(connected H G)
		(connected H I)
		(connected H K)
		(connected I F)
		(connected I H)
		(connected I L)
		(connected J G)
		(connected J K)
		(connected K H)
		(connected K J)
		(connected K L)
		(connected L I)
		(connected L K)
		(nodeInCamp A)
		(nodeInCamp B)
		(nodeInCamp C)
	)
	(:goal (and
			(paletInCamp Y)
			(paletInCamp W)
			(paletInCamp U)
		)
	)
)