package game.client.entities

import engine.CanvasCtx
import engine.entity.Entity
import engine.entity.feature.AbsolutePosition
import engine.geometry.Rectangle
import engine.utils.Layer

class TeamFrame (x: Double, y: => Double, team: Seq[Character]) extends Entity with AbsolutePosition {

	val layer: Layer = Layer.Interface
	def boundingBox: Rectangle = Rectangle(x, y , team.size * 90 - 10, 32)

	def draw(ctx: CanvasCtx): Unit = {
		ctx.textAlign = "center"
		ctx.textBaseline = "hanging"
		ctx.font = "400 12px 'Roboto Mono'"

		for (c <- team) {
			// Background
			ctx.fillStyle = "#eee"
			ctx.fillRect(0, 0, 80, 15)

			// Health bar
			ctx.fillStyle = c.skeleton.color.value.toString
			ctx.fillRect(0, 0, 80 * c.skeleton.health.current, 15)

			// Border
			ctx.strokeRect(0, 0, 80, 15)

			// Name
			ctx.fillStyle = "#000"
			ctx.fillText(c.skeleton.name.value, 40, 20)

			ctx.translate(90, 0)
		}
	}
}
