package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.DeathProperty

import scala.collection.mutable.ListBuffer

trait Values {

}

trait BecomeTheBiggestValues[A] extends Values {

  def getPlayerCellEntity: A

  def getEntityList: ListBuffer[A]
}

case class BecomeTheBiggestValuesImpl(player: DeathProperty, entities: ListBuffer[DeathProperty]) extends BecomeTheBiggestValues[DeathProperty] {

  override def getPlayerCellEntity: DeathProperty = player

  override def getEntityList = entities
}

abstract class AbstractEndGame[A <: Values] {

  protected def victoryCondition(values: A): Boolean
}

case class BecomeTheBiggestEndGame() extends AbstractEndGame[BecomeTheBiggestValues[DeathProperty]] {

  override def victoryCondition(values: BecomeTheBiggestValues[DeathProperty]): Boolean = {
    val playerCellEntity = values.getPlayerCellEntity
    val playerRadius = playerCellEntity.getDimensionComponent.radius
    values.getEntityList filter (entity => !entity.eq(playerCellEntity)) forall (entity => entity.getDimensionComponent.radius < playerRadius)
  }
}
