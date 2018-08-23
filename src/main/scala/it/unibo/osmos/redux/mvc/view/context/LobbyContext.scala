package it.unibo.osmos.redux.mvc.view.context

import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.events.{EventWrapperObserver, LobbyEventWrapper, UserAdded, UserRemoved}

/**
  * Basic LobbyContext trait, seen as a LobbyEvent wrapper and a User container
  */
trait LobbyContext extends EventWrapperObserver[LobbyEventWrapper]{

  def getCurrentUsers: Seq[User]

}

object LobbyContext {

  class LobbyContextImpl(val Listener: LobbyContextListener) extends LobbyContext {

    private var users: Seq[User] = Seq.empty

    override def getCurrentUsers: Seq[User] = users

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
