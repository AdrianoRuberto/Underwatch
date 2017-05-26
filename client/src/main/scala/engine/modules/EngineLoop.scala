package engine
package modules

import org.scalajs.dom

trait EngineLoop { this: Engine =>
	private var running = false
	private var locked = true

	def isRunning: Boolean = running
	def isLocked: Boolean = locked

	private var timestamp: Double = Double.NaN
	def time: Double = timestamp

	var cpuTime: Double = 0.0
	var drawTime: Double = 0.0

	def loop(now: Double): Unit = if (running) {
		// Capture current time at the beginning of the frame
		val startTime = dom.window.performance.now()

		// Compute delta time
		val dt = if (timestamp.isNaN) 0 else now - timestamp
		timestamp = now

		// Update everything
		for (entity <- updatableEntities) entity.update(dt)
		camera.update(dt)

		// Draw
		val drawStartTime = dom.window.performance.now()
		drawVisibleEntities()

		// Update CPU time
		val endTime = dom.window.performance.now()
		cpuTime = (cpuTime + endTime - startTime) / 2
		drawTime = (drawTime + endTime - drawStartTime) / 2

		// Request a new animation frame
		dom.window.requestAnimationFrame(loop _)
	}

	def start(): Unit = {
		running = true
		timestamp = Double.NaN
		dom.window.requestAnimationFrame(loop _)
	}

	def lock(): Unit = locked = true
	def unlock(): Unit = locked = false

	def stop(): Unit = {
		running = false
		ctx.clearRect(0, 0, canvas.width, canvas.height)
	}
}
