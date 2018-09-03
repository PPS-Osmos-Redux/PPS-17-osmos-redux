package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.entities.composed.PlayerCellEntity
import it.unibo.osmos.redux.ecs.entities.properties.composed.{DeathProperty, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.entities.{EntityType, PlayerCellEntity}
import it.unibo.osmos.redux.ecs.systems.victoryconditions.{AbsorbAllOtherPlayersCondition, AbsorbCellsWithTypeVictoryCondition, BecomeHugeVictoryCondition, BecomeTheBiggestVictoryCondition}
import it.unibo.osmos.redux.multiplayer.server.Server
import it.unibo.osmos.redux.mvc.model.VictoryRules
import it.unibo.osmos.redux.mvc.view.context.GameStateHolder
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GamePending, GameWon}
import it.unibo.osmos.redux.utils.Logger

/** System managing the level's ending rules
  *
  * @param levelContext object to notify the view of the end game result
  * @param victoryRules enumeration representing the level's victory rules
  */
case class MultiPlayerEndGameSystem(server: Server, levelContext: GameStateHolder, victoryRules: VictoryRules.Value) extends AbstractSystem2[PlayerCellEntity, DeathProperty] {

  private val victoryCondition = victoryRules match {
    case VictoryRules.becomeTheBiggest => BecomeTheBiggestVictoryCondition()
    case VictoryRules.becomeHuge => BecomeHugeVictoryCondition()
    case VictoryRules.absorbTheRepulsors => AbsorbCellsWithTypeVictoryCondition(EntityType.Repulsive)
    case VictoryRules.absorbTheHostileCells => AbsorbCellsWithTypeVictoryCondition(EntityType.Sentient)
    case VictoryRules.absorbAllOtherPlayers => AbsorbAllOtherPlayersCondition()
    case _ => throw new NotImplementedError()
  }

  override def update(): Unit = {
    if (isGameRunning) {
      val (deadPlayers, alivePlayers) = server.getLobbyPlayers partition (p => entities.map(_.getUUID) contains p.getUsername)
      val aliveCells = entitiesSecondType.filterNot(c => deadPlayers.map(_.getUUID) contains c.getUUID)

      //check
      for (
        (player, cell, isServer) <- alivePlayers
          .map(i => (i, entities.find(_.getUUID == i.getUUID), i.getUUID == server.getUUID)) //get player, cell and isServer
          .filter(i => i._2.nonEmpty && victoryCondition.check(i._2.get, aliveCells)) //filter only players that have won
        if levelContext.gameCurrentState == GamePending //when a winner is found do not check other players
      ) yield {
        Logger.log(s"Victory detected for: ${player.getUsername} - ${player.getUUID} (server: $isServer)")("MultiPlayerEndGameSystem")
        if (isServer) {
          server.stopGame()
          levelContext.notify(GameWon)
        } else {
          server.stopGame(player.getUsername)
          levelContext.notify(GameLost)
        }
      }

      //notify all dead players and remove them
      if (isGameRunning) deadPlayers.foreach(p => server.removePlayerFromGame(p.getUsername, notify = true))
    }
  }

  private def isGameRunning: Boolean = levelContext.gameCurrentState == GamePending
}

