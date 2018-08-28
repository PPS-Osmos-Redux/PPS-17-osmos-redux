package it.unibo.osmos.redux.multiplayer.client

import akka.actor.{Actor, Props}
import it.unibo.osmos.redux.multiplayer.client.ClientActor.Ready
import it.unibo.osmos.redux.multiplayer.server.ServerActor._
import it.unibo.osmos.redux.mvc.view.events._

/**
  * Client actor implementation
  * @param client The client to bind.
  */
class ClientActor(private val client: Client) extends Actor {

  override def receive: Receive = {
    case LobbyClosed => client.clearLobby()
    case PlayerEnteredLobby(player) => client.addPlayerToLobby(player)
    case PlayerLeftLobby(username) => client.removePlayerFromLobby(username)
    case UpdateGame(entities) => client.notifyRedraw(entities)
    case GameStarted(uuid) =>
      //save the uuid of the player entity that represents this client
      client.setUUID(uuid)
      //let interface change scene
      client.notifyGameStatusChanged(GamePending)
      //reply to server that all is ok
      sender ! Ready
    case GameEnded(victory) => client.notifyGameStatusChanged( if(victory) GameWon else GameLost )
  }
}

object ClientActor {

  def props(client: Client) : Props = Props(new ClientActor(client))

  final case object Ready //to reply to the server after game started received
  final case object Connect //to tell the server that you want to connect with it
  final case class EnterLobby(username: String) //to tell the server that you want to enter the lobby (server gets the actor ref from the sender object at his side)
  final case class LeaveLobby(username: String) //to tell the server that you leave the lobby
  final case class PlayerInput(event: MouseEventWrapper) //to send a new input event to the server
  final case class LeaveGame(username: String) //to tell server that you are leaving the game
}
