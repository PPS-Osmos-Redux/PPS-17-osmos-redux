package it.unibo.osmos.redux.ecs.systems

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
case class MultiPlayerEndGameSystem(server: Server, levelContext: GameStateHolder, victoryRules: VictoryRules.Value) extends AbstractSystem[DeathProperty] {

  private val victoryCondition = victoryRules match {
    case VictoryRules.becomeTheBiggest => BecomeTheBiggestVictoryCondition()
    case _ => throw new NotImplementedError()
  }

  override def getGroupProperty: Class[DeathProperty] = classOf[DeathProperty]

  override def update(): Unit = {
    if (levelContext.gameCurrentState == GamePending) {

      val playerEntities = entities.filter(_.isInstanceOf[PlayerCellEntity]) //filter player cells only
      val (alivePlayers, deadPlayers) = server.getLobbyPlayers partition(p => playerEntities.map(_.getUUID) contains p.getUsername)

      //notify all dead players and remove them
      deadPlayers.foreach(p => server.removePlayerFromGame(p.getUsername))

      //check
      for (
        (player, _, isServer) <- alivePlayers
          .map(i => (i, playerEntities.find(_.getUUID == i.getUUID), i.getUUID == server.getUUID)) //get player, cell and isServer
          .filter(i => i._2.nonEmpty && victoryCondition.check(i._2.get, entities)) //filter only players that have won
        if levelContext.gameCurrentState == GamePending //we should not check any other player if a winner is found
      ) yield {
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

