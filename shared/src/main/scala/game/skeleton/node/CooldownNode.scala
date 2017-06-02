package game.skeleton.node

import game.skeleton.AbstractSkeleton

class CooldownNode (implicit skeleton: AbstractSkeleton) extends InterpolatedNode(1.0) {
	/** Whether this spell is ready of not */
	def ready: Boolean = current == 1.0

	/** Cooldown progress as percent */
	def progress: Double = current

	/** Start the cooldown for the given duration */
	def start(duration: Double): Unit = {
		if (!ready) throw new IllegalStateException("Cannot start cooldown while already cooling down")
		value = 0.0
		interpolate(1.0, duration)
	}
}