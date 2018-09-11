package it.unibo.osmos.redux.multiplayer.client

import akka.actor.{Actor, ActorRef, Props, Terminated}
import it.unibo.osmos.redux.multiplayer.client.ClientActor.{Ready, StartWatching}
import it.unibo.osmos.redux.multiplayer.server.ServerActor._
import it.unibo.osmos.redux.mvc.view.events._
import it.unibo.osmos.redux.utils.Logger

/** Client actor implementation.
  * @param client The client to bind.
  */
class ClientActor(private val client: Client) extends Actor {

  implicit val who: String = "ClientActor"

  override def receive: Receive = {

    case StartWatching(serverRef: ActorRef) => context.watch(serverRef)

    case LobbyClosed => context.unwatch(sender); client.closeLobby(false); client.kill()

    case PlayerEnteredLobby(player) => client.addPlayerToLobby(player)

    case PlayerLeftLobby(username) => client.removePlayerFromLobby(username)

    case UpdateGame(entities) => client.notifyRedraw(entities)

    case GameStarted(uuid, levelInfo, mapShape) => client.startGame(uuid, levelInfo, mapShape); sender ! Ready

    case GameEnded(victory) => context.unwatch(sender); client.stopGame(victory)

    case Terminated(_) => client.closeLobby(false); client.kill()

    case unknownMessage => Logger.log("Received unknown message: " + unknownMessage)
  }
}

/** Client actor helper object */
object ClientActor {
  def props(client: Client) : Props = Props(new ClientActor(client))

  /** Replies to the server, after receiving a game started message, that it's ready to receive updates */
  final case object Ready

  /** Tells himself to start watching the server actor.
    * @param serverRef The server actor reference
    */
  final case class StartWatching(serverRef: ActorRef)

  /** Asks the server to begin handshaking.
    * @param actorRef It's own actor reference
    */
  final case class Connect(actorRef: ActorRef)

  /** Tells the server that a specific player is disconnecting.
    * @param username The username of the player
    */
  final case class Disconnect(username: String)

  /** Asks the server to enter the lobby.
    * @param clientID The client temp id (received from handshaking)
    * @param username The chosen username
    */
  final case class EnterLobby(clientID: String, username: String)

  /** Tells the server that a player leaves the lobby.
    * @param username The username of the player
    */
  final case class LeaveLobby(username: String)

  /** Sends to the server the user triggered mouse event to process, to let him move the entity.
    * @param event The mouse event.
    */
  final case class PlayerInput(event: MouseEventWrapper)

  /** Tells the server that a player leaves the game.
    * @param username The username of the player
    */
  final case class LeaveGame(username: String)
}
