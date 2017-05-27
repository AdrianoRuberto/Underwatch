package engine

import engine.Keyboard.Monitor
import org.scalajs.dom
import scala.collection.mutable

class Keyboard private[engine] (engine: Engine) {
	private var commands = Map.empty[String, () => Unit]

	var shift: Boolean = false
	var ctrl: Boolean = false
	var alt: Boolean = false

	private val states = mutable.Map.empty[String, Boolean]

	def key(name: String): Boolean = states.getOrElse(name, false)

	def down(name: String)(implicit monitor: Monitor): Boolean = monitorQuery(name, expected = true)
	def up(name: String)(implicit monitor: Monitor): Boolean = monitorQuery(name, expected = false)

	private def monitorQuery(name: String, expected: Boolean)(implicit monitor: Monitor): Boolean = {
		states.get(name) match {
			case None => false
			case Some(state) =>
				var (t, a, b) = monitor.states.getOrElse(name, Keyboard.defaultMonitorState)
				if (t != engine.time) {
					t = engine.time
					a = b
					b = state
					monitor.states.update(name, (t, a, b))
				}
				b == expected && a != expected
		}
	}

	def registerCommandKey(key: String)(cmd: => Unit): Unit = {
		commands += (key -> (() => cmd))
	}

	private[engine] def handler(event: dom.KeyboardEvent): Unit = if (!event.repeat) {
		shift = event.shiftKey
		ctrl = event.ctrlKey
		alt = event.altKey
		val state = event.`type` == "keydown"
		states.update(event.key, state)
		if (state && ctrl) {
			event.key match {
				case "d" => engine.drawWorldBoundingBox = !engine.drawWorldBoundingBox
				case "f" => engine.drawBoundingBoxes = !engine.drawBoundingBoxes
				case key =>
					commands.get(key) match {
						case Some(fn) => fn()
						case None => return
					}
			}
			if (engine.isRunning) event.preventDefault()
		}
	}
}

object Keyboard {
	class Monitor {
		private[Keyboard] val states: mutable.Map[String, (Double, Boolean, Boolean)] = mutable.Map.empty
	}
	private val defaultMonitorState = (0.0, false, false)
}
