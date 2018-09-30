package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.EntityManager
import it.unibo.osmos.redux.ecs.entities.properties.composed.DeathProperty
import it.unibo.osmos.redux.utils.Constants.General._

/** System managing the cells removal */
case class CellsEliminationSystem() extends AbstractSystem[DeathProperty] {

  override def update(): Unit = {
    entities.filter(_.getDimensionComponent.radius < radiusThreshold)
      .foreach(EntityManager.delete(_))
  }
}
