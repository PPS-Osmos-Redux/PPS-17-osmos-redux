package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.properties.composed.DeathProperty
import it.unibo.osmos.redux.ecs.entities.{EntityType, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.victoryconditions.{AbsorbCellsWithTypeVictoryCondition, BecomeHugeVictoryCondition, BecomeTheBiggestVictoryCondition}
import it.unibo.osmos.redux.mvc.controller.levels.structure.VictoryRules
import it.unibo.osmos.redux.mvc.view.context.GameStateHolder
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GamePending, GameWon}

/** System managing the level's ending rules
  *
  * @param levelContext object to notify the view of the end game result
  * @param victoryRules enumeration representing the level's victory rules
  */
case class EndGameSystem(levelContext: GameStateHolder, victoryRules: VictoryRules.Value) extends AbstractSystem2[PlayerCellEntity, DeathProperty] {

  private val victoryCondition = victoryRules match {
    case VictoryRules.becomeTheBiggest => BecomeTheBiggestVictoryCondition()
    case VictoryRules.becomeHuge => BecomeHugeVictoryCondition()
    case VictoryRules.absorbTheRepulsors => AbsorbCellsWithTypeVictoryCondition(EntityType.Repulsive)
    case VictoryRules.absorbTheHostileCells => AbsorbCellsWithTypeVictoryCondition(EntityType.Sentient)
    case _ => throw new NotImplementedError()
  }

  override def update(): Unit = {
    if (levelContext.gameCurrentState == GamePending) {
      if (entities.isEmpty) {
        levelContext.notify(GameLost)
      } else {
        entities foreach (playerEntity => {
          if (victoryCondition.check(playerEntity, entitiesSecondType)) levelContext.notify(GameWon)
        })
      }
    }
  }
}
