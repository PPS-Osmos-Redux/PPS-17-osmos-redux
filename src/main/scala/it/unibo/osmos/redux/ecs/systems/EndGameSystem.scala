package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{DeathProperty, PlayerCellEntity, Property}
import it.unibo.osmos.redux.mvc.model.VictoryRules

case class EndGameSystem(isSimulation: Boolean, levelInfo: VictoryRules.Value) extends AbstractSystem[DeathProperty] {

  override def getGroupProperty: Class[_ <: Property] = classOf[DeathProperty]

  override def update(): Unit = {
    if (!isSimulation) {
      val optionalPlayer: Option[DeathProperty] = entities.find(entity => entity.isInstanceOf[PlayerCellEntity]).headOption
      var playerCellEntity: DeathProperty = null

      optionalPlayer match {
        case Some(player) => playerCellEntity = player
        case None => // gameover
      }

      val playerRadius = playerCellEntity.getDimensionComponent.radius
      var victory = false
      levelInfo match {
        case VictoryRules.becomeTheBiggest =>
          victory = entities filter (entity => !entity.eq(playerCellEntity)) forall (entity => entity.getDimensionComponent.radius < playerRadius)
        case _ => throw new NotImplementedError()
      }
    }

  }
}
