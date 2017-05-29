package game.server

import game.UID
import game.protocol.ServerMessage
import game.skeleton.{Event, Transmitter}
import scala.language.implicitConversions

trait BasicGameImplicits {
	this: BasicGame =>

	/**
	  * An instance of [[Transmitter]] that send [[Event.ClosetEvent]] to every players of the game.
	  * It is used as implicit parameter during the construction of the [[skeletons]] map.
	  */
	protected implicit object SkeletonTransmitter extends Transmitter {
		def ! (event: Event.ClosetEvent): Unit = {
			broadcast ! ServerMessage.SkeletonEvent(event)
		}
	}

	/** Some quality of life operations on UIDs */
	protected implicit final class UIDOps(private val uid: UID) {
		@inline def ! (msg: Any): Unit = actors.get(uid) match {
			case Some(ag) => ag ! msg
			case None => throw new IllegalArgumentException(s"No actor target found for UID `$uid`")
		}
	}
}
