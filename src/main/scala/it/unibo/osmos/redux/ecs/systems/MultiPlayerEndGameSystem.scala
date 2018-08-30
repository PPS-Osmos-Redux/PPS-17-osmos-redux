package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.ecs.entities.{DeathProperty, PlayerCellEntity}
import it.unibo.osmos.redux.multiplayer.server.Server
import it.unibo.osmos.redux.multiplayer.server.ServerActor.GameEnded
import it.unibo.osmos.redux.mvc.model.VictoryRules
import it.unibo.osmos.redux.mvc.view.context.GameStateHolder
import it.unibo.osmos.redux.mvc.view.events.{GameLost, GamePending, GameWon}

/** System managing the level's ending rules
  *
  * @param levelContext object to notify the view of the end game result
  * @param victoryRules enumeration representing the level's victory rules
  */
case class MultiPlayerEndGameSystem(server: Server, levelContext: GameStateHolder, victoryRules: VictoryRules.Value) extends AbstractSystemWithTwoTypeOfEntity[PlayerCellEntity, DeathProperty] {

  private val victoryCondition = victoryRules match {
    case VictoryRules.becomeTheBiggest => BecomeTheBiggestVictoryCondition()
    case VictoryRules.becomeHuge => BecomeHugeVictoryCondition()
    case VictoryRules.absorbTheRepulsors => AbsorbCellsWithTypeVictoryCondition(EntityType.Repulse)
    case VictoryRules.absorbTheHostileCells => AbsorbCellsWithTypeVictoryCondition(EntityType.Sentient)
    case VictoryRules.absorbAllOtherPlayers => AbsorbAllOtherPlayersCondition()
    case _ => throw new NotImplementedError()
  }

  override protected def getGroupProperty: Class[PlayerCellEntity] = classOf[PlayerCellEntity]

  override protected def getGroupPropertySecondType: Class[DeathProperty] = classOf[DeathProperty]

  override def update(): Unit = {
    if (levelContext.gameCurrentState == GamePending) {

      val (deadPlayers, alivePlayers) = server.getLobbyPlayers partition(p => entities.map(_.getUUID) contains p.getUsername)
      val aliveCells = entitiesSecondType.filterNot(c => deadPlayers.map(_.getUUID) contains c.getUUID)

      //notify all dead players and remove them
      deadPlayers.foreach(p => server.removePlayerFromGame(p.getUsername))

      //check
      for (
        (player, _, isServer) <- alivePlayers
          .map(i => (i, entities.find(_.getUUID == i.getUUID), i.getUUID == server.getUUID)) //get player, cell and isServer
          .filter(i => i._2.nonEmpty && victoryCondition.check(i._2.get, aliveCells)) //filter only players that have won
        if levelContext.gameCurrentState == GamePending //we should not check any other player if a winner is found
      ) yield {

        //TODO: avoid to kill the server, incapsulate those login inside the stop
        if (isServer) {
          server.stopGame()
          levelContext.notify(GameWon)
        } else {
          server.deliverMessage(player.getUsername, GameEnded(true))
          server.broadcastMessage(GameEnded(false), player.getUsername)
          levelContext.notify(GameLost)
          server.kill()
        }
      }
    }
  }
}

