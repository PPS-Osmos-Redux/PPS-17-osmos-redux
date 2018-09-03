package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.EntityManager
import it.unibo.osmos.redux.ecs.entities.properties.composed.DeathProperty

case class CellsEliminationSystem() extends AbstractSystem[DeathProperty] {

  val radiusThreshold: Double = 1

  override def update(): Unit = {
    entities.filter(_.getDimensionComponent.radius < radiusThreshold)
            .foreach(EntityManager.delete(_))
  }
}
