package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.{DeathProperty, PlayerCellEntity, Property}
import it.unibo.osmos.redux.mvc.model.VictoryRules
import it.unibo.osmos.redux.mvc.view.levels.LevelContext

case class EndGameSystem(levelContext: LevelContext, isSimulation: Boolean, levelInfo: VictoryRules.Value) extends AbstractSystem[DeathProperty] {

  private var levelStatus = LevelStatus.Nothing
  private val victoryRules = levelInfo match {
    case VictoryRules.becomeTheBiggest => BecomeTheBiggestEndGame()
    case _ => throw new NotImplementedError()
  }

  override def getGroupProperty: Class[_ <: Property] = classOf[DeathProperty]

  override def update(): Unit = {
    if (!isSimulation && levelStatus.equals(LevelStatus.Nothing)) {
      val optionalPlayer: Option[DeathProperty] = entities.find(entity => entity.isInstanceOf[PlayerCellEntity]).headOption

      optionalPlayer match {
        case Some(player) =>
          victoryRules.victoryCondition(BecomeTheBiggestValuesImpl(player, entities))
          // TODO: levelStatus = LevelStatus.Completed
        case None => levelStatus = LevelStatus.Failed
      }
    }

  }
}

object LevelStatus extends Enumeration {
  val Completed, Failed, Nothing = Value
}
