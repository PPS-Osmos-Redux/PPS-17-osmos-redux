package it.unibo.osmos.redux.mvc.view

import it.unibo.osmos.redux.mvc.controller.Controller
import it.unibo.osmos.redux.mvc.view.levels.LevelContext
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

    app.stage = OsmosReduxPrimaryStage(this)
    private var controller: Option[Controller] = Option.empty

    override def setController(controller: Controller): Unit = {
      this.controller = Option(controller)
    }

    override def onLevelContextSetup(levelContext: LevelContext, level: Int, simulation: Boolean): Unit = controller match {
      case Some(c) => c.startLevel(levelContext, level, simulation)
      case _ =>
    }

    override def onPauseLevel(): Unit = controller match {
      case Some(c) => c.pauseLevel()
      case _ =>
    }

    override def onResumeLevel(): Unit = controller match {
      case Some(c) => c.resumeLevel()
      case _ =>
    }

    override def onStopLevel(): Unit = controller match {
      case Some(c) => c.stopLevel()
      case _ =>
    }
  }

}

