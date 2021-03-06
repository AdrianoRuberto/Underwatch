package game

import org.scalajs.dom

package object client {
	implicit class EventTargetOps(private val eventTarget: dom.EventTarget) extends AnyVal {
		def on[T <: dom.Event](event: Event[T])(handler: T => Unit): Unit = {
			eventTarget.addEventListener(event.name, handler)
		}
	}

	abstract class Event[T <: dom.Event](val name: String)

	object Event {
		object Blur extends Event[dom.FocusEvent]("blur")
		object Click extends Event[dom.MouseEvent]("click")
		object Close extends Event[dom.CloseEvent]("close")
		object Error extends Event[dom.Event]("error")
		object Input extends Event[dom.Event]("input")
		object KeyDown extends Event[dom.KeyboardEvent]("keydown")
		object KeyUp extends Event[dom.KeyboardEvent]("keyup")
		object Load extends Event[dom.UIEvent]("load")
		object Message extends Event[dom.MessageEvent]("message")
		object Open extends Event[dom.Event]("open")
		object Resize extends Event[dom.UIEvent]("resize")
		object TransitionEnd extends Event[dom.TransitionEvent]("transitionend")
	}
}
