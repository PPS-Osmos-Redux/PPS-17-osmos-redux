package it.unibo.osmos.redux.ecs.systems.victoryconditions

import it.unibo.osmos.redux.ecs.entities.{DeathProperty, EntityType}

import scala.collection.mutable.ListBuffer

/** class implementing become the biggest victory condition */
case class BecomeTheBiggestVictoryCondition() extends AbstractVictoryCondition {

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    val playerRadius = playerCellEntity.getDimensionComponent.radius
    entityList.filter(e => !e.getTypeComponent.typeEntity.equals(EntityType.AntiMatter) && !e.eq(playerCellEntity))
      .forall(e => e.getDimensionComponent.radius < playerRadius)
  }
}
