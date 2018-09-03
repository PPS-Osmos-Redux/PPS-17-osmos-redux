package it.unibo.osmos.redux.ecs.systems.victoryconditions

import it.unibo.osmos.redux.ecs.entities.{DeathProperty, EntityType}

import scala.collection.mutable.ListBuffer

/** class implementing absorb all other players */
case class AbsorbAllOtherPlayersCondition() extends AbstractVictoryCondition {

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    !entityList.exists(entity => entity.getTypeComponent.typeEntity == EntityType.Controlled && entity.getUUID != playerCellEntity.getUUID)
  }
}
