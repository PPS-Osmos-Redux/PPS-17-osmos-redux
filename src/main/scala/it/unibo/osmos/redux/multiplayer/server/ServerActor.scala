package it.unibo.osmos.redux.multiplayer.server

import akka.actor.{Actor, ActorIdentity, ActorRef, Identify, Props}
import it.unibo.osmos.redux.multiplayer.client.ClientActor._
import it.unibo.osmos.redux.multiplayer.common.ClientsManager
import it.unibo.osmos.redux.multiplayer.players.BasePlayer
import it.unibo.osmos.redux.multiplayer.server.ServerActor.{Disconnected, _}
import it.unibo.osmos.redux.mvc.controller.levels.structure.{LevelInfo, MapShape}
import it.unibo.osmos.redux.mvc.view.drawables.DrawableEntity
import it.unibo.osmos.redux.utils.Logger

/** Server actor implementation.
  * @param server The server to bind.
  */
class ServerActor(private val server: Server) extends Actor {

  implicit val who: String = "ServerActor"

  //manager for clients handshaking
  private lazy val clientsManager = ClientsManager(System.currentTimeMillis())

  override def receive: Receive = {
    case Connect(actorRef: ActorRef) =>
      //generate a new random id
      val tempID = clientsManager.nextTempID
      //add temp actor reference to reply to ask
      clientsManager.addClient(tempID, sender)
      //send to the real actor reference request for identification
      actorRef ! Identify(tempID)

    case ActorIdentity(id, Some(ref)) =>
      //get temp actor reference
      clientsManager.getClient(id.toString) match {
        case Some(askRef) =>
          //add the real actor reference to the clients manager
          clientsManager.addClient(id.toString, ref)
          //fulfill ask request
          askRef ! Connected(id.toString)
        case None =>
          //reject ask request
          sender ! Disconnected
          throw new IllegalStateException("Unable to fulfill ask request because the actor ref was not found.")
      }

    case ActorIdentity(id, None) =>
      clientsManager.getClient(id.toString) match {
        case Some(askRef) =>
          //reject ask request
          askRef ! Disconnected
        case None =>
          //reject ask request
          sender ! Disconnected
          throw new IllegalStateException("Unable to reject ask request because the actor ref was not found.")
      }

    case EnterLobby(tempID, username) =>
      clientsManager.getClient(tempID) match {
        case Some(ref) =>
          //get client info
          val (address, port) = (ref.path.address.host.get, ref.path.address.port.get)
          if (server.addPlayerToLobby(ref, BasePlayer(username, address, port))) {
            //send info about the lobby
            sender ! LobbyInfo(server.getLobbyPlayers.map(_.toBasicPlayer))
            //add watch to this actor
            context.watchWith(ref, Disconnect(username))
          } else sender ! LobbyFull
        case None =>
          //handshaking failed
          sender ! Disconnected
          throw new IllegalArgumentException(s"Unable to found actor ref for client with tempID: $tempID")
      }

    case LeaveLobby(username) => context.unwatch(sender); server.removePlayerFromLobby(username)

    case PlayerInput(event) => server.notifyClientInputEvent(event)

    case LeaveGame(username) => context.unwatch(sender); server.removePlayerFromGame(username)

    case Disconnect(username) =>
      server.getState match {
        case ServerState.Lobby => server.removePlayerFromLobby(username)
        case ServerState.Game => server.removePlayerFromGame(username); server.removePlayerFromLobby(username)
        case _ => //do nothing
      }

    case unknownMessage => Logger.log("Received unknown message: " + unknownMessage)
  }
}

/** Server actor helper object */
object ServerActor {
  def props(server: Server) : Props = Props(new ServerActor(server))

  /** Replies to a client connection request initiating the handshaking
    * @param tempID The temp id
    */
  final case class Connected(tempID: String)
  /** Aborts an handshaking with a client */
  final case object Disconnected
  /** Replies to a client enter lobby request with its info
    * @param players The players already inside the lobby
    */
  final case class LobbyInfo(players: Seq[BasePlayer])
  /** Aborts a client enter lobby request because the chosen username is already taken */
  final case object UsernameAlreadyTaken
  /** Aborts a client enter lobby request because the lobby is full */
  final case object LobbyFull
  /** Tells the client that the lobby have been closed */
  final case object LobbyClosed
  /** Tells the client that a new player entered in the lobby
    * @param player The new player
    */
  final case class PlayerEnteredLobby(player: BasePlayer)
  /**
    * Tells the client that a player left the lobby
    * @param username The username of the player
    */
  final case class PlayerLeftLobby(username: String)
  /** Sends the client the entities to draw
    * @param entities The entities to draw
    */
  final case class UpdateGame(entities: Seq[DrawableEntity])
  /** Tells the client that the game have been started
    * @param uuid The uuid of the entity assigned to the client by the server that represents him in the game
    * @param levelInfo The level info
    * @param mapShape The map shape
    */
  final case class GameStarted(uuid: String, levelInfo: LevelInfo, mapShape: MapShape)
  /** Tells the client that the game is ended and what's the final result
    * @param victory If client won or lost.
    */
  final case class GameEnded(victory: Boolean)
}


