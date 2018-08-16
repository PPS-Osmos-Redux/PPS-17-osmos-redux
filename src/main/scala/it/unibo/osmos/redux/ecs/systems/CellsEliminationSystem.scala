package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{DeathProperty, EntityManager, Property}

case class CellsEliminationSystem(override val priority: Int) extends AbstractSystem[DeathProperty](priority) {
  override def getGroupProperty: Class[_ <: Property] = classOf[DeathProperty]

  val radiusThreshold:Double = 5

  override def update(): Unit = {
    entities.filter(_.getDimensionComponent.radius < radiusThreshold)
            .foreach(EntityManager.delete(_))
  }

  def entitiesSize:Int = entities.size
}
