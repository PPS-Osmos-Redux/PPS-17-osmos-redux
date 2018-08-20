package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{DeathProperty, EntityManager, Property}

case class CellsEliminationSystem() extends AbstractSystem[DeathProperty] {
  override def getGroupProperty: Class[DeathProperty] = classOf[DeathProperty]

  val radiusThreshold: Double = 1

  override def update(): Unit = {
    entities.filter(_.getDimensionComponent.radius < radiusThreshold)
            .foreach(EntityManager.delete(_))
  }
}
