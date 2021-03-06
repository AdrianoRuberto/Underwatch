package game.server

import akka.actor.ActorRef
import game.PlayerInfo

case class GamePlayer(actor: ActorRef, info: PlayerInfo)
