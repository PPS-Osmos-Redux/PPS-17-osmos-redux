package it.unibo.osmos.redux.main.mvc.view

import it.unibo.osmos.redux.main.mvc.controller.Controller
import it.unibo.osmos.redux.main.mvc.view.levels.LevelContext
import it.unibo.osmos.redux.main.mvc.view.stages.{OsmosReduxPrimaryStage, PrimaryStageListener}
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

    override def onLevelContextSetup(levelContext: LevelContext): Unit = controller match {
      case Some(c) => c.startLevel(levelContext)
      case _ =>
    }
  }

}

