package engine
package modules

import engine.entity.{Entity, feature}

trait EngineEntities { this: Engine =>
	private[engine] var entities = Set.empty[Entity]
	private[engine] var updatableEntities = Set.empty[Entity with feature.Updatable]

	def registerEntity(entity: Entity): Unit = entity.registerWith(this)
	def unregisterEntity(entity: Entity): Unit = entity.unregisterFrom(this)

	def unregisterAllEntities(): Unit = for (e <- entities) e.unregister(soft = true)
}
