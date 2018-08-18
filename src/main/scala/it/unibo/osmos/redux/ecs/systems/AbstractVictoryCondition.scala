package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{DeathProperty, Property}

import scala.collection.mutable.ListBuffer

abstract class AbstractVictoryCondition[A <: Property] {

  protected def check(playerCellEntity: A, entityList: ListBuffer[A]): Boolean
}

case class BecomeTheBiggestVictoryCondition() extends AbstractVictoryCondition[DeathProperty] {

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    val playerRadius = playerCellEntity.getDimensionComponent.radius
    entityList filter (entity => !entity.eq(playerCellEntity)) forall (entity => entity.getDimensionComponent.radius < playerRadius)
  }
}

case class BecomeHugeVictoryCondition() extends AbstractVictoryCondition[DeathProperty] {

  // TODO: adjust the value
  private val radiusPercentageToBeHuge = 70

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    val playerRadius = playerCellEntity.getDimensionComponent.radius
    var totalRadius: Double = 0
    entityList foreach (entity => totalRadius += entity.getDimensionComponent.radius)
    playerRadius / totalRadius * 100 > radiusPercentageToBeHuge
  }
}

case class AbsorbHostileCellsVictoryCondition() extends AbstractVictoryCondition[DeathProperty] {

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    // TODO: change with Hostile Cells trait
    entityList exists (entity => entity.getDimensionComponent.radius < 10)
  }
}

case class AbsorbTheAttractorVictoryCondition() extends AbstractVictoryCondition[DeathProperty] {

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    // TODO: change with Attractor Cell trait
    entityList exists (entity => entity.getDimensionComponent.radius < 10)
  }
}
