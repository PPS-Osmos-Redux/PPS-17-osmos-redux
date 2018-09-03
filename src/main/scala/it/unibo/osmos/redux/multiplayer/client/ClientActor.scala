package it.unibo.osmos.redux.multiplayer.client

import akka.actor.{Actor, ActorRef, Props, Terminated}
import it.unibo.osmos.redux.multiplayer.client.ClientActor.{Ready, StartWatching}
import it.unibo.osmos.redux.multiplayer.server.ServerActor._
import it.unibo.osmos.redux.mvc.view.events._
import it.unibo.osmos.redux.utils.Logger

/**
  * Client actor implementation
  * @param client The client to bind.
  */
class ClientActor(private val client: Client) extends Actor {

  implicit val who: String = "ClientActor"

  override def preStart(): Unit = {
    Logger.log("Actor starting...")
  }

  override def receive: Receive = {

    case StartWatching(serverRef: ActorRef) => context.watch(serverRef)

    case LobbyClosed => context.unwatch(sender); client.closeLobby(false); client.kill()

    case PlayerEnteredLobby(player) => client.addPlayerToLobby(player)

    case PlayerLeftLobby(username) => client.removePlayerFromLobby(username)

    case UpdateGame(entities) => client.notifyRedraw(entities)

    case GameStarted(uuid, levelInfo, mapShape) => client.startGame(uuid, levelInfo, mapShape); sender ! Ready

    case GameEnded(victory) => context.unwatch(sender); client.stopGame(victory)

    case Terminated(_) =>
      Logger.log(s"Received Terminated message from server.")
      client.closeLobby(false)
      client.kill()

    case unknownMessage => Logger.log("Received unknown message: " + unknownMessage)("ClientActor")
  }

  override def postStop(): Unit = {
    Logger.log("Actor is shutting down...")
    super.postStop()
  }
}

object ClientActor {

  def props(client: Client) : Props = Props(new ClientActor(client))

  final case object Ready //to reply to the server after game started received
  final case class StartWatching(serverRef: ActorRef) //tell by himself to start watching server actor

  final case class Connect(actorRef: ActorRef) //to tell the server that you want to connect with it
  final case class Disconnect(username: String) //send by the client when a problem occurs and the actor dies

  final case class EnterLobby(clientID: String, username: String) //to tell the server that you want to enter the lobby (server gets the actor ref from the sender object at his side)
  final case class LeaveLobby(username: String) //to tell the server that you leave the lobby

  final case class PlayerInput(event: MouseEventWrapper) //to send a new input event to the server
  final case class LeaveGame(username: String) //to tell server that you are leaving the game
}
