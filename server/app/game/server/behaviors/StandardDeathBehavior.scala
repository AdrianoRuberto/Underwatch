package game.server.behaviors

import engine.geometry.Vector2D
import game.UID
import game.doodads.Doodad
import game.server.BasicGame

trait StandardDeathBehavior extends BasicGame { behavior =>

	def respawnLocationForPlayer(player: UID): Vector2D
	def respawnTimeForPlayer(player: UID): Double = 5000.0

	createTicker { _ =>
		players.map(uid => uid -> uid.skeleton)
			.filter(!_._2.dead.value)
			.filter(_._2.health.current <= 0)
			.foreach { case (uid, player) =>
				val deathScreen = createDoodad(Doodad.Interface.DeathScreen(respawnTimeForPlayer(uid)), uid)
				val position = respawnLocationForPlayer(uid)
				val time = respawnTimeForPlayer(uid)

				player.dead.value = true
				player.health.rate = player.health.max / time * 1000
				player.x.value = position.x
				player.y.value = position.y

				schedule(time) {
					player.health.rate = 0
					player.health.energize(player.health.max)
					player.dead.value = false
					destroyDoodad(deathScreen)
				}
			}
	}
}
