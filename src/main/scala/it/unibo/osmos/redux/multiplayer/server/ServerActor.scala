package it.unibo.osmos.redux.multiplayer.server

import akka.actor.{Actor, Props}
import it.unibo.osmos.redux.multiplayer.client.ClientActor._
import it.unibo.osmos.redux.multiplayer.players.{BasicPlayer, PlayerInfo}
import it.unibo.osmos.redux.multiplayer.server.ServerActor.{Established, LobbyFull, LobbyInfo}
import it.unibo.osmos.redux.mvc.view.drawables.DrawableEntity

/**
  * Server actor implementation
  * @param server The server to bind
  */
class ServerActor(private val server: Server) extends Actor {

  override def receive: Receive = {
    case Connect => sender ! Established
    case EnterLobby(username) =>
      //to tell the server that you want to enter the lobby (server gets the actor ref from the sender object at his side)
      //TODO: along with all current players in the lobby you can send UUID too
      val playerInfo = PlayerInfo(sender.path.address.host.getOrElse("0.0.0.0"), sender.path.address.port.getOrElse(0))
      if (server.addPlayerToLobby(sender, BasicPlayer(username, playerInfo))) sender ! LobbyInfo(server.getLobbyPlayers.map(_.toBasicPlayer))
      else sender ! LobbyFull
    case LeaveLobby(username) => server.removePlayerFromLobby(username)
    case PlayerInput(event) => server.notifyClientInputEvent(event)
    case LeaveGame(username) => server.removePlayerFromGame(username)
  }
}

/**
  * Server actor helper object
  */
object ServerActor {
  def props(server: Server) : Props = Props(new ServerActor(server))

  final case object Established //Server reply to connect request if all is ok
  final case object UsernameAlreadyTaken //after entering a lobby the server tells you if the username is already taken
  final case class LobbyInfo(players: Seq[BasicPlayer]) //After entering a lobby the server sends the lobby info
  final case object LobbyFull //server reply to a request of entering a lobby if it's full
  final case object LobbyClosed //when the server closes the lobby
  final case class PlayerEnteredLobby(player: BasicPlayer) //The server notify you that another player entered the lobby
  final case class PlayerLeftLobby(username: String) //Server notify you that a player left the lobby
  final case class UpdateGame(entities: Seq[DrawableEntity]) //server send all entities to draw
  final case class GameStarted(id: String) //Server wants to start the game, reply with Ready if all is ok (tell you who are you)
  final case class GameEnded(victory: Boolean) //Server have stopped the game (and tells you if you won or lose)
}


