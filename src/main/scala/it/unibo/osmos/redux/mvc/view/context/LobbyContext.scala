package it.unibo.osmos.redux.mvc.view.context

import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.events.{EventWrapperObserver, LobbyEventWrapper, UserAdded, UserRemoved}

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
      case UserAdded => if (!users.contains(event.user)) users = users :+ event.user
      case UserRemoved => if (users.contains(event.user)) users = users filterNot(u => u.username == event.user.username)
    }

  }

}

/**
  * Trait which gets notified when a LobbyContext event occurs
  */
trait LobbyContextListener {

}
