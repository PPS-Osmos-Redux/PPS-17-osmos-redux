package it.unibo.osmos.redux.mvc.view

import it.unibo.osmos.redux.mvc.controller.Controller
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, LobbyContext}
import it.unibo.osmos.redux.mvc.view.stages.{OsmosReduxPrimaryStage, PrimaryStageListener}
import scalafx.application.JFXApp

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}

/**
  * View base trait
  */
trait View {


  /**
    * Setter. This method sets the reference to the Controller instance
    * @param controller the Controller instance
    */
  def setController(controller: Controller)

}

object View {

  def apply(app: JFXApp): View = new ViewImpl(app)

  /**
    * View implementation, holding the main stage and the current scene
    * @param app a reference to the JFXApp, necessary to the correct setup of the whole application
    */
  class ViewImpl(private val app: JFXApp) extends View with PrimaryStageListener {

    implicit val ec: ExecutionContextExecutor = ExecutionContext.global

    app.stage = OsmosReduxPrimaryStage(this)
    private var controller: Option[Controller] = Option.empty

    override def setController(controller: Controller): Unit = {
      this.controller = Option(controller)
    }

    /**
      * Utility method that checks if controller is not empty and execute f() if that is the case
      * @param f the function which will be executed if controller is not empty
      */
    private def checkController(f:() => Unit): Unit = controller match {
      case Some(_) => f()
      case _ =>
    }

    override def onLevelContextCreated(levelContext: LevelContext, level: Int): Unit = checkController(() =>controller.get.initLevel(levelContext, level))

    override def onStartLevel(): Unit = checkController(() => controller.get.startLevel())

    override def onPauseLevel(): Unit = checkController(() => controller.get.pauseLevel())

    override def onResumeLevel(): Unit = checkController(() => controller.get.resumeLevel())

    override def onStopLevel(): Unit = checkController(() => controller.get.stopLevel())

    /**
      * After checking the controller, we ask to enter the lobby asynchronously and call the callback function after the future result
      * @param user the user requesting to enter the lobby
      * @param lobbyContext the lobby context, which may be used by the server to configure existing lobby users
      * @param callback the callback
      */
    override def onLobbyClick(user: User, lobbyContext: LobbyContext, callback: (User, LobbyContext, Boolean) => Unit): Unit =
      checkController(() => controller.get.initLobby(user, lobbyContext).future.onComplete{
        case Success(value) => callback(user, lobbyContext, value)
        case Failure(e) => //TODO: show alert dialog
      })

    override def onStartMultiplayerGameClick(): Unit = checkController(() => controller.get.initMultiPlayerLevel().future.onComplete{
      case Failure(e) => //TODO: show alert dialog
      case Success(_) => //do nothing
    })
  }
}

