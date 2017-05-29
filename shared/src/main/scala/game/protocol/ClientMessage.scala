package game.protocol

import boopickle.Default._
import game.UID

sealed trait ClientMessage

object ClientMessage {
	case class Ping(payload: Long) extends ClientMessage
	case class SearchGame(name: String, fast: Boolean) extends ClientMessage

	trait GameMessage extends ClientMessage

	private implicit val UIDPicker = UID.pickler
	implicit val pickler: Pickler[ClientMessage] = generatePickler[ClientMessage]
}
