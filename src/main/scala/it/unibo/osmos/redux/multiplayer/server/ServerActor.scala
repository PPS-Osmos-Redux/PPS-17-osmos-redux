package it.unibo.osmos.redux.multiplayer.server

import akka.actor.{Actor, ActorIdentity, ActorRef, Identify, Props}
import it.unibo.osmos.redux.multiplayer.client.ClientActor._
import it.unibo.osmos.redux.multiplayer.common.ClientsManager
import it.unibo.osmos.redux.multiplayer.players.BasePlayer
import it.unibo.osmos.redux.multiplayer.server.ServerActor.{Disconnected, _}
import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.drawables.DrawableEntity
import it.unibo.osmos.redux.utils.Logger

/**
  * Server actor implementation
  * @param server The server to bind
  */
class ServerActor(private val server: Server) extends Actor {

  implicit val who: String = "ServerActor"

  //manager for clients handshaking
  private val clientsManager = ClientsManager(System.currentTimeMillis())

  override def preStart(): Unit = {
    Logger.log("Actor starting...")
  }

  override def receive: Receive = {
    case Connect(actorRef: ActorRef) => //handshaking (async with ask pattern)
      val tempID = clientsManager.nextTempID //generate a new random id

      Logger.log(s"Acknowledged connection, sending tempID --> $tempID")

      clientsManager.addClient(tempID, sender) //add temp actor reference to reply to ask
      actorRef ! Identify(tempID) //send to the real actor reference request for identification

    case ActorIdentity(id, Some(ref)) => //handshaking succeeded
      Logger.log(s"Received identity from client ($id) --> $ref")

      //get temp actor reference
      clientsManager.getClient(id.toString) match {
        case Some(askRef) =>
          clientsManager.addClient(id.toString, ref) //add the real actor reference to the clients manager
          askRef ! Connected(id.toString) //fulfill ask request
        case None =>
          sender ! Disconnected //reject ask request
          throw new IllegalStateException("Unable to fulfill ask request because the actor ref was not found.")
      }

    case ActorIdentity(id, None) => //handshaking failed
      Logger.log(s"Received invalid identity from client ($id)")

      clientsManager.getClient(id.toString) match {
        case Some(askRef) =>
          askRef ! Disconnected //reject ask request
        case None =>
          sender ! Disconnected //reject ask request
          throw new IllegalStateException("Unable to reject ask request because the actor ref was not found.")
      }

    case EnterLobby(tempID, username) => //to tell the server that you want to enter the lobby (server gets the actor ref from the sender object at his side)
      Logger.log(s"Received request to enter lobby from client --> $username ($tempID)")

      clientsManager.getClient(tempID) match {
        case Some(ref) =>
          val (address, port) = (ref.path.address.host.getOrElse("0.0.0.0"), ref.path.address.port.getOrElse(0))
          if (server.addPlayerToLobby(ref, BasePlayer(username, address, port))) {
            sender ! LobbyInfo(server.getLobbyPlayers.map(_.toBasicPlayer))
            context.watchWith(ref, Disconnect(username)) //add watch to this actor
          } else sender ! LobbyFull
        case None =>
          sender ! Disconnected //handshaking failed
          throw new IllegalArgumentException(s"Unable to found actor ref for client with tempID: $tempID")
      }

    case LeaveLobby(username) =>
      Logger.log(s"Received LeaveLobby message from $username ($sender)")

      context.unwatch(sender); server.removePlayerFromLobby(username)

    case PlayerInput(event) => server.notifyClientInputEvent(event)

    case LeaveGame(username) =>
      Logger.log(s"Received LeaveGame message from $username")

      context.unwatch(sender); server.removePlayerFromGame(username)

    case Disconnect(username) =>
      Logger.log(s"Received Disconnect message from $username ($sender)")

      server.getState match {
        case ServerState.Lobby => server.removePlayerFromLobby(username)
        case ServerState.Game => server.removePlayerFromGame(username)
        case _ => //do nothing
      }

    case unknownMessage => Logger.log("Received unknown message: " + unknownMessage)("ServerActor")
  }

  override def postStop(): Unit = {
    Logger.log("Actor is shutting down...")
    super.postStop()
  }
}

/**
  * Server actor helper object
  */
object ServerActor {
  def props(server: Server) : Props = Props(new ServerActor(server))

  final case class Connected(tempID: String) //Server reply to connect request if all is ok
  final case object Disconnected //Server reply to client if it send an empty actor ref as identification

  final case object UsernameAlreadyTaken //after entering a lobby the server tells you if the username is already taken
  final case object LobbyFull //server reply to a request of entering a lobby if it's full

  final case class LobbyInfo(players: Seq[BasePlayer]) //After entering a lobby the server sends the lobby info
  final case object LobbyClosed //when the server closes the lobby

  final case class PlayerEnteredLobby(player: BasePlayer) //The server notify you that another player entered the lobby
  final case class PlayerLeftLobby(username: String) //Server notify you that a player left the lobby

  final case class UpdateGame(entities: Seq[DrawableEntity]) //server send all entities to draw

  final case class GameStarted(id: String, mapShape: MapShape) //Server wants to start the game, reply with Ready if all is ok (tell you who are you)
  final case class GameEnded(victory: Boolean) //Server have stopped the game (and tells you if you won or lose)
}


