package game.client

import boopickle.DefaultBasic.{Pickle, Unpickle}
import boopickle._
import game.protocol.ServerMessage.Severity
import game.protocol.{ClientMessage, ServerMessage, SystemMessage}
import java.nio.ByteBuffer
import org.scalajs.dom
import org.scalajs.dom.raw.WebSocket
import scala.scalajs.js
import scala.scalajs.js.typedarray._
import scala.util.Try
import utils.PersistentBoolean

object Server {
	private var socket: dom.WebSocket = null

	val verbose = PersistentBoolean("serverVerbose", default = false)
	val latencyEmulation = PersistentBoolean("serverLatency", default = false)
	var latency: Double = 0

	def searchGame(name: String, fast: Boolean): Unit = {
		require(socket == null, "Attempted to search for game while socket is still open")
		socket = new WebSocket(s"ws://${dom.document.location.host}/socket")
		socket.binaryType = "arraybuffer"
		socket.on(Event.Open) { _ => Server ! ClientMessage.SearchGame(name, fast) }
		socket.on(Event.Close)(socketClosed)
		socket.on(Event.Error)(socketClosed)
		socket.on(Event.Message) { event =>
			val buffer = TypedArrayBuffer.wrap(event.data.asInstanceOf[ArrayBuffer])
			implicit val unpickleState = (bb: ByteBuffer) => new UnpickleState(new DecoderSize(bb), false, false)
			val message = Unpickle[ServerMessage].fromBytes(buffer)
			if (latencyEmulation) js.timers.setTimeout(100)(handleMessage(message))
			else handleMessage(message)
		}
	}

	def disconnect(silent: Boolean = false): Unit = {
		require(silent || socket != null, "Attempted to disconnect from server while not connected")
		if (socket != null) {
			socket.close()
			socket = null
		}
	}

	def ! (message: ClientMessage): Unit = {
		//require(socket != null, "Attempted to send message to server while not connected")
		if (verbose && !message.isInstanceOf[SystemMessage]) dom.console.log(">>", message.toString)
		implicit def pickleState = new PickleState(new EncoderSize, false, false)
		val buffer = Pickle.intoBytes(message).toArrayBuffer
		if (latencyEmulation) js.timers.setTimeout(100)(socket.send(buffer))
		else socket.send(buffer)
	}

	def socketClosed(e: dom.Event): Unit = {
		socket = null
	}

	def handleMessage(message: ServerMessage): Unit = {
		if (verbose && !message.isInstanceOf[SystemMessage]) dom.console.log("<<", message.toString)
		message match {
			case ServerMessage.Bundle(messages) => messages.foreach(handleMessage)
			case ServerMessage.Ping(ms, payload) =>
				latency = ms
				this ! ClientMessage.Ping(payload)
			case ServerMessage.Debug(severity, args) => debugOutput(severity, args)
			case ServerMessage.ServerError => App.reboot(true)
			case ServerMessage.GameEnd => App.reboot()
			case lm: ServerMessage.LobbyMessage => Lobby.message(lm)
			case gm: ServerMessage.GameMessage => Game.message(gm)
		}
	}

	private def debugOutput(severity: Severity, args: Seq[String]): Unit = if (args.nonEmpty) {
		val parsed = args.map { arg =>
			Try(js.JSON.parse(arg).asInstanceOf[js.Any]).getOrElse(arg.asInstanceOf[js.Any])
		}
		val console = dom.console.asInstanceOf[js.Dynamic]
		val handler = severity match {
			case ServerMessage.Severity.Verbose => console.debug
			case ServerMessage.Severity.Info => console.log
			case ServerMessage.Severity.Warn => console.warn
			case ServerMessage.Severity.Error => console.error
		}
		for (f <- handler.asInstanceOf[js.UndefOr[js.Function]]) {
			f.call(console, parsed: _*)
		}
	}

	implicit class ByteBufferOps(private val buffer: ByteBuffer) extends AnyVal {
		def toArrayBuffer: ArrayBuffer = {
			val length = buffer.remaining()
			TypedArrayBufferOps.byteBufferOps(buffer).arrayBuffer().slice(0, length)
		}
	}
}
