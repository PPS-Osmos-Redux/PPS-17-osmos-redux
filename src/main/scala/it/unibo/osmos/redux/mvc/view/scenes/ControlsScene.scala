package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.ViewConstants
import it.unibo.osmos.redux.mvc.view.stages.PrimaryStageListener
import scalafx.geometry.Pos
import scalafx.scene.control.Label
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.Text
import scalafx.stage.Stage

class ControlsScene(override val parentStage: Stage, listener: PrimaryStageListener, previousSceneListener: BackClickListener) extends DefaultBackScene(parentStage, previousSceneListener) {

  private val commands = new VBox() {
    children = Seq(new Label("Movement"),
      new Label("Pause"),
      new Label("Speed up time"),
      new Label("Slow down time"))
  }

  private val controls = new VBox() {
    children = Seq(new Text("Click on the screen to eject mass and move in the opposite direction"),
      new Text("Press esc to stop game (single player only)"),
      new Text("Press up or right arrow key to speed up game time (single player only)"),
      new Text("Press down or left arrow key to speed up game time (single player only)"))
  }

  private val container = new HBox(20) {
    alignment = Pos.Center
    children = Seq(commands, controls)
    styleClass.add("controls-hbox")
  }

  /**
    * The central container
    */
  protected val controlSceneMainContainer: VBox = new VBox(15) {
    alignment = Pos.Center
    children = Seq(container, goBack)
    styleClass.add("settings-vbox")
  }

  /* Setting the root container*/
  root = controlSceneMainContainer
}
