package it.unibo.osmos.redux.mvc.view.context

import it.unibo.osmos.redux.mvc.controller.levels.structure.LevelInfo
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.events._
import it.unibo.osmos.redux.utils.Logger

/** Basic LobbyContext trait, seen both as a LobbyEventWrapper observable and observer */
trait LobbyContext extends EventWrapperObserver[LobbyEventWrapper] with EventWrapperObservable[LobbyEventWrapper] {

  /** The lobby current users */
  private[this] var _users: Seq[User] = Seq.empty

  def users: Seq[User] = _users

  def users_=(value: Seq[User]): Unit = _users = value

  /** Setter. Sets the lobby context listener
    *
    * @param lobbyContextListener the lobby context listener
    */
  def setListener(lobbyContextListener: LobbyContextListener)

  /** Called when the LobbyContext gets a lobby event from the scene
    *
    * @param event the lobby event
    */
  def notifyLobbyEvent(event: LobbyEventWrapper)

}

/** Companion object */
object LobbyContext {

  def apply(): LobbyContext = new LobbyContextImpl()

  /** LobbyContext implementation */
  class LobbyContextImpl() extends LobbyContext {

    /** The lobby context listener */
    protected var listener: Option[LobbyContextListener] = Option.empty
    /** A reference to the lobby event observer, used to notify the server that we went away */
    private var lobbyContextObserver: Option[EventWrapperObserver[LobbyEventWrapper]] = Option.empty

    override def setListener(lobbyContextListener: LobbyContextListener): Unit = listener = Option(lobbyContextListener)

    override def notify(event: LobbyEventWrapper): Unit = listener match {
      case Some(l) =>
        event match {
          /** A user entered the lobby */
          case LobbyEventWrapper(UserAdded, Some(user)) => if (!users.contains(user)) users = users :+ user; l.updateUsers(users)

          /** A user exited from the lobby */
          case LobbyEventWrapper(UserRemoved, Some(user)) => if (users.contains(user)) users = users filterNot (u => u.username == user.username); l.updateUsers(users)

          /** The game has started, we can create a new MultiPlayerLevelScene */
          case LobbyEventWrapper(StartGame(multiPlayerLevelContext, levelInfo), _) => l.onMultiPlayerGameStarted(multiPlayerLevelContext, levelInfo)

          /** The lobby has been aborted, we have to go back */
          case LobbyEventWrapper(AbortLobby, _) => l.onLobbyAborted()
          case _ => Logger.log(s"Unknown lobby event received: $event")("LobbyContext")
        }
      case _ => Logger.log("Cannot notify listener about the new lobby event because is not set")("LobbyContext")

    }

    override def subscribe(eventWrapperObserver: EventWrapperObserver[LobbyEventWrapper]): Unit = lobbyContextObserver = Option(eventWrapperObserver)

    override def unsubscribe(eventWrapperObserver: EventWrapperObserver[LobbyEventWrapper]): Unit = lobbyContextObserver = Option.empty

    override def notifyLobbyEvent(event: LobbyEventWrapper): Unit = lobbyContextObserver match {
      case Some(lco) => lco.notify(event)
      case _ => Logger.log("Cannot notify observer with the new lobby event because it is not set")("LobbyContext")
    }
  }

}

/** Trait which gets notified when a LobbyContext event occurs */
trait LobbyContextListener {

  /** Update the users list that must be shown to the user
    *
    * @param users the new user seq
    */
  def updateUsers(users: Seq[User])

  /** Called once per lobby. A new MultiPLayerLevelScene will be created the scene
    *
    * @param multiPlayerLevelContext the level context
    * @param levelInfo               the level info
    */
  def onMultiPlayerGameStarted(multiPlayerLevelContext: MultiPlayerLevelContext, levelInfo: LevelInfo)

  /** Called once per lobby if the lobby is deleted */
  def onLobbyAborted()

}
