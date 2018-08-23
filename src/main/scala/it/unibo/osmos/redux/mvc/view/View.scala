package it.unibo.osmos.redux.mvc.view

import scala.concurrent.ExecutionContext
import it.unibo.osmos.redux.mvc.controller.Controller
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.levels.{LevelContext, LevelContextType}
import it.unibo.osmos.redux.mvc.view.stages.{OsmosReduxPrimaryStage, PrimaryStageListener}
import scalafx.application.JFXApp

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

    implicit val ec = ExecutionContext.global

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

    override def onLevelContextCreated(levelContext: LevelContext, level: Int, levelContextType: LevelContextType.Value): Unit = checkController(() => {
      if (levelContextType eq LevelContextType.multiplayer) {
        controller.get.initLevel(levelContext, level, levelContextType)
      } else {
        controller.get.initMultiPlayerLevel(levelContext, level)
      }
    })

    override def onStartLevel(): Unit = checkController(() => controller.get.startLevel())

    override def onPauseLevel(): Unit = checkController(() => controller.get.pauseLevel())

    override def onResumeLevel(): Unit = checkController(() => controller.get.resumeLevel())

    override def onStopLevel(): Unit = checkController(() => controller.get.stopLevel())

    /**
      * After checking the controller, we ask to enter the lobby asynchronously and call the callback function after the futur result
      * @param user the user requesting to enter the lobby
      * @param callback the callback
      */
    override def onLobbyClick(user: User, callback: (User, Boolean) => Unit): Unit = checkController(() => controller.get.initLobby(user).future.onComplete((res) => callback(user, res.get)))

  }

}

