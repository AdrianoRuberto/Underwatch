package game.client

import engine.Engine
import engine.entity.Entity
import game.client.entities.{Character, DebugStats, Player}
import game.protocol.ServerMessage
import game.protocol.ServerMessage._
import game.skeleton.Closet
import game.skeleton.concrete.CharacterSkeleton
import game.{TeamInfo, UID}
import org.scalajs.dom
import org.scalajs.dom.html
import utils.PersistentBoolean

object Game {
	private lazy val canvas = dom.document.querySelector("#canvas").asInstanceOf[html.Canvas]
	private lazy val engine = new Engine(canvas)

	private val debugStatsShown = PersistentBoolean("displayStats", default = false)
	private lazy val debugStatsEntity = new DebugStats(10, 10)

	private val closet = new Closet
	private var characterEntities: Map[UID, Entity] = Map.empty

	private var teams: Seq[TeamInfo] = Nil
	private var playerUID: UID = _

	def setup(): Unit = {
		dom.window.on(Event.Resize) { _ => resizeCanvas() }
		resizeCanvas()
		engine.setup()
		engine.keyboard.registerKey("ctrl-s")(toggleDebugStats())
	}

	def resizeCanvas(): Unit = {
		canvas.width = dom.window.innerWidth.toInt
		canvas.height = dom.window.innerHeight.toInt
	}

	def start(teams: Seq[TeamInfo], me: UID): Unit = {
		this.teams = teams
		this.playerUID = me
		engine.start()
		if (debugStatsShown) {
			engine.registerEntity(debugStatsEntity)
		}
		/*
		engine.setWorldSize(2000, 2000)

		val enemy = new Character("Malevolent foe", 0) {
			setPosition(500, 500)
		}
		engine.registerEntity(enemy)

		val a = new Character("A Bot", 0) {
			healthColor = "#5a5"
			setPosition(100, 123)
		}

		val b = new Character("Heregellas", 0) {
			healthColor = "#5a5"
			setPosition(248, 46)
		}

		val c = new Character("0123456789", 0) {
			healthColor = "#5a5"
			setPosition(144, 178)
		}


		engine.registerEntity(a)
		engine.registerEntity(b)
		engine.registerEntity(c)

		val player = new Player("Blash")
		engine.registerEntity(player)
		engine.camera.follow(player)

		engine.registerEntity(new TeamFrame(10, if (debugStatsShown) 35 else 10, Seq(player, a, b, c)))

		engine.registerEntity(new PlayerFrame(85, -80, player))*/
	}

	def reset(): Unit = {
		engine.unregisterAllEntities()
		closet.clear()
	}

	def unlock(): Unit = engine.unlock()
	def lock(): Unit = engine.lock()
	def stop(): Unit = engine.stop()

	private def toggleDebugStats(): Unit = {
		if (debugStatsShown) engine.unregisterEntity(debugStatsEntity)
		else engine.registerEntity(debugStatsEntity)
		debugStatsShown.toggle()
	}

	private def instantiateCharacter(characterUID: UID, skeletonUID: UID): Unit = {
		val skeleton = closet.getAs[CharacterSkeleton](skeletonUID)
		val entity =
			if (characterUID == playerUID) new Player(skeleton)
			else new Character(skeleton)
		characterEntities += (characterUID -> entity)
		engine.registerEntity(entity)
	}

	def message(gm: ServerMessage.GameMessage): Unit = if (engine.isRunning) gm match {
		case SkeletonEvent(event) => closet.handleEvent(event)
		case InstantiateCharacter(characterUID, skeletonUID) => instantiateCharacter(characterUID, skeletonUID)
		case SetCameraLocation(x, y) => engine.camera.setPoint(x, y)
		case SetCameraFollow(characterUID) => engine.camera.follow(characterEntities(characterUID))
		case GameStart =>
			App.hidePanels()
			Game.unlock()
	}
}
