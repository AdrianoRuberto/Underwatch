package game.protocol

import boopickle.Default._
import game.maps.GameMap
import game.skeleton.Event
import game.{GameMode, TeamInfo, UID, skeleton}

sealed trait ServerMessage

object ServerMessage {
	// Core messages
	case object ServerError extends ServerMessage
	case object GameEnd extends ServerMessage
	case class Ping(payload: Double) extends ServerMessage
	case class Bundle(messages: Seq[ServerMessage]) extends ServerMessage

	// Lobby messages
	sealed trait LobbyMessage extends ServerMessage
	case class QueueUpdate(count: Int) extends LobbyMessage
	case class GameFound(mode: GameMode, team: Seq[TeamInfo], me: UID, warmup: Int) extends LobbyMessage

	// Game messages
	sealed trait GameMessage extends ServerMessage
	case object GameStart extends GameMessage
	case class SkeletonEvent(event: skeleton.Event.ClosetEvent) extends GameMessage
	case class InstantiateCharacter(characterUID: UID, skeletonUID: UID) extends GameMessage
	case class SetCameraLocation(x: Double, y: Double) extends GameMessage
	case class SetCameraFollow(characterUID: UID) extends GameMessage

	// Debug message
	sealed trait Severity
	object Severity {
		case object Verbose extends Severity
		case object Info extends Severity
		case object Warn extends Severity
		case object Error extends Severity
		implicit val pickler: Pickler[Severity] = generatePickler[Severity]
	}
	case class Debug(severity: Severity, args: Seq[String]) extends ServerMessage

	private implicit val UIDPicker = UID.pickler
	private implicit val GameModePickler = GameMode.pickler
	private implicit val GameMapPickler = GameMap.pickler
	private implicit val SkeletonPickler = Event.closetEventPickler
	implicit val pickler: Pickler[ServerMessage] = generatePickler[ServerMessage]
}
