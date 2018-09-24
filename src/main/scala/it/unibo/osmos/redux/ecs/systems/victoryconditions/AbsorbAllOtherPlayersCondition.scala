package it.unibo.osmos.redux.ecs.systems.victoryconditions

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.ecs.entities.properties.composed.DeathProperty

import scala.collection.mutable.ListBuffer

/** class implementing absorb all other players
  *
  * victory is fulfilled when there are no more players to absorb left
  */
case class AbsorbAllOtherPlayersCondition() extends VictoryCondition {

  override def check(playerCellEntity: DeathProperty, entityList: ListBuffer[DeathProperty]): Boolean = {
    !entityList.exists(entity => entity.getTypeComponent.typeEntity == EntityType.Controlled && entity.getUUID != playerCellEntity.getUUID)
  }
}
