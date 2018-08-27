package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.ecs.entities.{DeathProperty, Property}

import scala.collection.mutable.ListBuffer

/** Abstract class for victory condition strategy
  *
  * @tparam A
  */
abstract class AbstractVictoryCondition[A <: Property] {

  /** Checks if the victory condition is fulfilled
    *
    * @param playerCellEntity player's entity
    * @param entityList       entities present in this game instant
    * @return the evaluation result
    */
  def check(playerCellEntity: A, entityList: ListBuffer[A]): Boolean
}

/** class implementing become the biggest victory condition */
case class BecomeTheBiggestVictoryCondition() extends AbstractVictoryCondition[DeathProperty] {

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    val playerRadius = playerCellEntity.getDimensionComponent.radius
    entityList.filter(e => !e.getTypeComponent.typeEntity.equals(EntityType.AntiMatter) && !e.eq(playerCellEntity))
      .forall(e => e.getDimensionComponent.radius < playerRadius)
  }
}

/** class implementing become huge victory condition */
case class BecomeHugeVictoryCondition() extends AbstractVictoryCondition[DeathProperty] {

  // TODO: adjust the value
  private val radiusPercentageToBeHuge = 70

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    val playerRadius = playerCellEntity.getDimensionComponent.radius
    var totalRadius: Double = 0
    entityList.filter(e => !e.getTypeComponent.typeEntity.equals(EntityType.AntiMatter))
      .foreach(entity => totalRadius += entity.getDimensionComponent.radius)
    playerRadius / totalRadius * 100 > radiusPercentageToBeHuge
  }
}

/** class implementing absorb cells that share a common type victory condition */
case class AbsorbCellsWithTypeVictoryCondition(entityType: EntityType.Value) extends AbstractVictoryCondition[DeathProperty] {

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    !entityList.exists(entity => entity.getTypeComponent.typeEntity.equals(entityType))
  }
}
