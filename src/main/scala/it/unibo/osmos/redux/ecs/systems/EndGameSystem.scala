package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{DeathProperty, PlayerCellEntity, Property}
import it.unibo.osmos.redux.mvc.model.VictoryRules
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GamePending, GameWon}
import it.unibo.osmos.redux.mvc.view.levels.GameStateHolder

case class EndGameSystem(levelContext: GameStateHolder, /*isSimulation: Boolean,*/ levelInfo: VictoryRules.Value) extends AbstractSystem[DeathProperty] {

  private val victoryCondition = levelInfo match {
    case VictoryRules.becomeTheBiggest => BecomeTheBiggestVictoryCondition()
    case _ => throw new NotImplementedError()
  }

  override def getGroupProperty: Class[_ <: Property] = classOf[DeathProperty]

  override def update(): Unit = {
    if ( /*!isSimulation &&*/ levelContext.gameCurrentState == GamePending) {
      val optionalPlayer: Option[DeathProperty] = entities.find(entity => entity.isInstanceOf[PlayerCellEntity])

      optionalPlayer match {
        case Some(player) =>
          if (victoryCondition.check(player, entities)) {
            levelContext.gameCurrentState_=(GameWon)
          }
        case None => levelContext.gameCurrentState_=(GameLost)
      }
    }
  }
}
