package game.server.modes.koth

import engine.geometry.{Rectangle, Vector2D}
import game.UID
import game.doodads.Doodad
import game.server.behaviors.StandardDeathBehavior
import game.server.{BasicGame, GameMap, GameTeam}
import game.skeleton.Skeleton
import game.spells.Spell
import utils.Color

/**
  * Take control of the objective and defend it against the enemy team
  */
class KingOfTheHillGame (roster: Seq[GameTeam]) extends BasicGame(roster) with StandardDeathBehavior {
	private val map = GameMap.Nepal

	loadMap(map)
	setDefaultTeamColors()
	setDefaultCamera()

	// Teams
	private val Seq(teamA, teamB) = teams

	// Game constants
	private final val CapturePerSecond = 20.0
	private final val ProgressPerSecond = 1.0
	private final val OvertimeGrace = 3000.0

	// Base spells
	for (player <- players) {
		player gainSpell (0, Spell.Sword)
		player gainSpell (3, Spell.Sprint)
		player gainSpell (2, Spell.BioticField)
	}

	// Status
	private val status = createDynamicDoodad(Doodad.Hud.KothStatus, Skeleton.KothStatus)
	status.teamA.value = teamA
	status.teamB.value = teamB

	// Capture Area
	private val captureArea = Rectangle(-170, -150, 340, 320)
	createRegion(captureArea, enterArea, leaveArea)

	private var playerFromAOnPoint = 0
	private var playerFromBOnPoint = 0

	private def enterArea(uid: UID): Unit = {
		if (uid.team == teamA) playerFromAOnPoint += 1
		else if (uid.team == teamB) playerFromBOnPoint += 1
	}

	private def leaveArea(uid: UID): Unit = {
		if (uid.team == teamA) playerFromAOnPoint -= 1
		else if (uid.team == teamB) playerFromBOnPoint -= 1
	}

	// Capture Area Doodad
	private val area = createDynamicDoodad(Doodad.Area.DynamicArea, Skeleton.DynamicArea)
	area.shape.value = captureArea

	// Capture progress
	private var currentCapture = Int.MaxValue // MaxValue used as "no capture"
	private def interpolateCapture(direction: Int): Unit = if (currentCapture != direction) {
		currentCapture = direction
		status.capture.interpolateAtSpeed(direction * 100, CapturePerSecond)
	}

	private var lastAOnPoint = 0.0
	private var lastBOnPoint = 0.0
	private var overtimeStatus = 0
	private var overtimeCreated = false

	private lazy val overtime = {
		overtimeCreated = true
		createDynamicDoodad(Doodad.Hud.Overtime, Skeleton.Overtime)
	}

	// Area implementation
	private val areaTicker = createTicker { dt =>
		// Current point controller
		val controlling = status.controlling.value

		// Capture cases
		if (playerFromAOnPoint > 0 && playerFromBOnPoint == 0 && controlling != teamA) {
			// Team A captures the point
			interpolateCapture(1)
		} else if (playerFromBOnPoint > 0 && playerFromAOnPoint == 0 && controlling != teamB) {
			// Team B captures the point
			interpolateCapture(-1)
		} else if (playerFromBOnPoint == 0 && controlling == teamA) {
			// Capture decays toward team A
			interpolateCapture(1)
		} else if (playerFromAOnPoint == 0 && controlling == teamB) {
			// Capture decays toward team B
			interpolateCapture(-1)
		} else if (playerFromAOnPoint == 0 && playerFromBOnPoint == 0 && controlling == UID.zero) {
			// Capture decays toward neutral
			interpolateCapture(0)
		} else {
			// The point is contested in any way
			currentCapture = Int.MaxValue
			status.capture.stop()
		}

		// Update last time each team touched the point
		if (playerFromAOnPoint > 0) lastAOnPoint = time
		if (playerFromBOnPoint > 0) lastBOnPoint = time

		// Point capture triggers
		val captureValue = status.capture.current
		if (captureValue == 100 && controlling != teamA) {
			// Team A took the point
			status.controlling.value = teamA
			area.fillColor.value = Color(119, 119, 255, 0.1)
			area.strokeColor.value = Color(119, 119, 255, 0.8)
			status.progressA.interpolateAtSpeed(99.9, ProgressPerSecond)
			status.progressB.stop()
		} else if (captureValue == -100 && controlling != teamB) {
			// Team B took the point
			status.controlling.value = teamB
			area.fillColor.value = Color(255, 85, 85, 0.1)
			area.strokeColor.value = Color(255, 85, 85, 0.8)
			status.progressB.interpolateAtSpeed(99.9, ProgressPerSecond)
			status.progressA.stop()
		}

		// Win condition
		val controllingNow = status.controlling.value
		if (status.progressA.current >= 99.9 && controllingNow == teamA) {
			if (playerFromBOnPoint == 0 && time - lastBOnPoint > OvertimeGrace) {
				status.progressA.value = 100
				win(teamA)
			} else {
				overtime.enabled.value = true
				if (playerFromBOnPoint == 0) {
					if (overtimeStatus != 1) {
						overtime.left.interpolate(0, lastBOnPoint + OvertimeGrace - time)
						overtimeStatus = 1
					}
				} else if (overtimeStatus != 2) {
					overtime.left.value = 100
					overtimeStatus = 2
				}
			}
		} else if (status.progressB.current >= 99.9 && controllingNow == teamB) {
			if (playerFromAOnPoint == 0 && time - lastAOnPoint > OvertimeGrace) {
				status.progressB.value = 100
				win(teamB)
			} else {
				overtime.enabled.value = true
				if (playerFromAOnPoint == 0) {
					if (overtimeStatus != 1) {
						overtime.left.interpolate(0, lastAOnPoint + OvertimeGrace - time)
						overtimeStatus = 1
					}
				} else if (overtimeStatus != 2) {
					overtime.left.value = 100
					overtimeStatus = 2
				}
			}
		} else {
			overtimeStatus = 0
			if (overtimeCreated) overtime.enabled.value = false
		}
	}

	// Win handler
	def win(team: UID): Unit = {
		areaTicker.remove()
		players.engine.disableInputs()

		// Display victory screen
		removeDeathScreens()
		createDoodad(Doodad.Hud.VictoryScreen(
			if (team == teamA) "Blue team wins!" else "Red team wins!",
			if (team == teamA) "rgb(119, 119, 255)" else "rgb(255, 85, 85)"
		))

		// Schedule termination 5 sec later
		terminate(5000)
	}

	def respawnLocation(player: UID): Vector2D = map.spawns(teamsIndex(player.team))
}
