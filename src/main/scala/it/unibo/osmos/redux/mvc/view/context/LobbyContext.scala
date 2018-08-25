package it.unibo.osmos.redux.mvc.view.context

import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.events._

/**
  * Basic LobbyContext trait, seen as a LobbyEvent wrapper and a User container
  */
trait LobbyContext extends EventWrapperObserver[LobbyEventWrapper] with EventWrapperObservable[LobbyEventWrapper]{

  /**
    * The lobby current users
    */
  private[this] var _users: Seq[User] = Seq.empty
  def users: Seq[User] = _users
  def users_=(value: Seq[User]): Unit = _users = value

  /**
    * Setter. Sets the lobby context listener
    * @param lobbyContextListener the lobby context listener
    */
  def setListener(lobbyContextListener: LobbyContextListener)

  /**
    * Called when the LobbyContext gets a lobby event from the scene
    * @param event the lobby event
    */
  def notifyLobbyEvent(event: LobbyEventWrapper)

}

/**
  * Companion object
  */
object LobbyContext {

  def apply(): LobbyContext = new LobbyContextImpl()

  /**
    * LobbyContext implementation
    */
  class LobbyContextImpl() extends LobbyContext {

    /**
      * The lobby context listener
      */
    protected var listener: Option[LobbyContextListener] = Option.empty
    override def setListener(lobbyContextListener: LobbyContextListener): Unit = listener = Option(lobbyContextListener)

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

    /**
      * A reference to the lobby event listener, used to notify the server that we went away
      */
    private var lobbyContextObserver: Option[EventWrapperObserver[LobbyEventWrapper]] = Option.empty
    override def subscribe(eventWrapperObserver: EventWrapperObserver[LobbyEventWrapper]): Unit = lobbyContextObserver = Option(eventWrapperObserver)
    override def unsubscribe(eventWrapperObserver: EventWrapperObserver[LobbyEventWrapper]): Unit = lobbyContextObserver = Option.empty

    override def notifyLobbyEvent(event: LobbyEventWrapper): Unit = lobbyContextObserver match {
      case Some(lco) => lco.notify(event)
      case _ =>
    }
  }

}

/**
  * Trait which gets notified when a LobbyContext event occurs
  */
trait LobbyContextListener {

}
