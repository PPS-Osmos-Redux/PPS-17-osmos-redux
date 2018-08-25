package it.unibo.osmos.redux.mvc.view.context

import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.events._

/**
  * Basic LobbyContext trait, seen as a LobbyEvent wrapper and a User container
  */
trait LobbyContext extends EventWrapperObserver[LobbyEventWrapper]{

  /**
    * The lobby current users
    */
  private[this] var _users: Seq[User] = Seq.empty
  def users: Seq[User] = _users
  def users_=(value: Seq[User]): Unit = _users = value

}

/**
  * Companion object
  */
object LobbyContext {

  def apply(lobbyContextListener: LobbyContextListener): LobbyContext = new LobbyContextImpl(lobbyContextListener)

  /**
    * LobbyContext implementation
    * @param Listener the lobby context listener
    */
  class LobbyContextImpl(val Listener: LobbyContextListener) extends LobbyContext {

    //TODO: call the listener
    override def notify(event: LobbyEventWrapper): Unit = event.lobbyEvent match {
      /* A user entered the lobby */
      case UserAdded => if (!users.contains(event.user)) users = users :+ event.user
      /* A user exited from the lobby */
      case UserRemoved => if (users.contains(event.user)) users = users filterNot(u => u.username == event.user.username)
      /* The game has started, we can create a new LevelScene */
      case StartGame(levelContext) =>
      /* The lobby has been aborted, we have to go back */
      case AbortLobby =>
    }

  }

}

/**
  * Trait which gets notified when a LobbyContext event occurs
  */
trait LobbyContextListener {

}
