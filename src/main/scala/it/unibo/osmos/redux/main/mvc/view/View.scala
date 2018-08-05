package it.unibo.osmos.redux.main.mvc.view

import it.unibo.osmos.redux.main.mvc.controller.Controller
import it.unibo.osmos.redux.main.mvc.view.stages.OsmosReduxPrimaryStage
import scalafx.application.JFXApp

/**
  * View base trait
  */
trait View {

  /**
    * This method sets the view parameters
    */
  def setup()
  /**
    * Setter. This method sets the reference to the Controller instance
    * @param controller the Controller instance
    */
  def setController(controller: Controller)

  /**
    * This method orders the draw to update the visible scene
    */
  def draw(): Unit

}

object View {

  def apply(app: JFXApp): View = new ViewImpl(app)

  /**
    * View implementation, holding the main stage and the current scene
    * @param app a reference to the JFXApp, necessary to the correct setup of the whole application
    */
  class ViewImpl(private val app: JFXApp) extends View {

    private var controller: Option[Controller] = Option.empty

    override def setup(): Unit = {
      app.stage = OsmosReduxPrimaryStage()
    }

    override def setController(controller: Controller): Unit = {
      this.controller = Option(controller)
    }

    override def draw(): Unit = {

    }
  }

}

