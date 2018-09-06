package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.ViewConstants
import it.unibo.osmos.redux.mvc.view.stages.PrimaryStageListener
import scalafx.geometry.Pos
import scalafx.scene.control.TableView
import scalafx.scene.layout.VBox
import scalafx.stage.Stage

class StatsScene(override val parentStage: Stage, listener: PrimaryStageListener, previousSceneListener: BackClickListener) extends DefaultBackScene(parentStage, previousSceneListener) {

  private val statsTable = new TableView[String]() {
    maxWidth = ViewConstants.Window.halfWindowWidth
    prefHeight = ViewConstants.Window.defaultWindowHeight / 4
  }

  /**
    * The central container
    */
  protected val container: VBox = new VBox(15) {
    alignment = Pos.Center
    children = Seq(statsTable, goBack)
    styleClass.add("settings-vbox")
  }

  /* Setting the root container*/
  root = container
}
