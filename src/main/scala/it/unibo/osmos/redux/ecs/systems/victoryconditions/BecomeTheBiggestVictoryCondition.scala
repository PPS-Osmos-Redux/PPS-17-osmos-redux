package it.unibo.osmos.redux.ecs.systems.victoryconditions

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.ecs.entities.properties.composed.DeathProperty

import scala.collection.mutable.ListBuffer

/** class implementing become the biggest victory condition
  *
  * victory is fulfilled when the player's radius is
  * greater than the other matter cells radius
  */
case class BecomeTheBiggestVictoryCondition() extends AbstractVictoryCondition {

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    val playerRadius = playerCellEntity.getDimensionComponent.radius
    entityList.filter(e => !e.getTypeComponent.typeEntity.equals(EntityType.AntiMatter) && !e.eq(playerCellEntity))
      .forall(e => e.getDimensionComponent.radius < playerRadius)
  }
}
