package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.ecs.entities.{DeathProperty, PlayerCellEntity}
import it.unibo.osmos.redux.mvc.model.VictoryRules
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GamePending, GameWon}
import it.unibo.osmos.redux.mvc.view.levels.GameStateHolder

/** System managing the level's ending rules
  *
  * @param levelContext object to notify the view of the end game result
  * @param victoryRules enumeration representing the level's victory rules
  */
case class EndGameSystem(levelContext: GameStateHolder, victoryRules: VictoryRules.Value) extends AbstractSystemWithTwoTypeOfEntity[PlayerCellEntity, DeathProperty] {

  private val victoryCondition = victoryRules match {
    case VictoryRules.becomeTheBiggest => BecomeTheBiggestVictoryCondition()
    case VictoryRules.becomeHuge => BecomeHugeVictoryCondition()
    case VictoryRules.absorbTheRepulsors => AbsorbCellsWithTypeVictoryCondition(EntityType.Repulse)
    case VictoryRules.absorbTheHostileCells => AbsorbCellsWithTypeVictoryCondition(EntityType.Sentient)
    case _ => throw new NotImplementedError()
  }

  override protected def getGroupProperty: Class[PlayerCellEntity] = classOf[PlayerCellEntity]

  override protected def getGroupPropertySecondType: Class[DeathProperty] = classOf[DeathProperty]

  override def update(): Unit = {
    if (levelContext.gameCurrentState == GamePending) {
      if (entities.isEmpty) {
        levelContext.notify(GameLost)
      } else {
        entities foreach (playerEntity => {
          if (victoryCondition.check(playerEntity, entitiesSecondType)) {
            levelContext.notify(GameWon)
          }
        })
      }
    }
  }

}
