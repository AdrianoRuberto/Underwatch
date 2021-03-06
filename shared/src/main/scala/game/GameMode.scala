package game

import macros.pickle

@pickle sealed trait GameMode {
	val name: String
	val desc: String
}

object GameMode {
	@pickle case object CaptureTheFlag extends GameMode {
		val name = "Capture the Flag"
		val desc = "Capture the enemy team's flag while defending yours"
	}

	@pickle case object KingOfTheHill extends GameMode {
		val name = "King of the Hill"
		val desc = "Take control of the objective and defend it against the enemy team"
	}

	@pickle case object TwistingNether extends GameMode {
		val name = "Twisting Nether"
		val desc = "In space no one can hear you scream..."
	}
}
