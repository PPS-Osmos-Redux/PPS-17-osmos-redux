package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.FileManager
import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import it.unibo.osmos.redux.mvc.view.context.LevelContext
import it.unibo.osmos.redux.mvc.view.stages.PrimaryStageListener
import scalafx.geometry.Pos
import scalafx.scene.control.{Label, Slider}
import scalafx.scene.layout._
import scalafx.stage.Stage

class SettingsScene(override val parentStage: Stage, listener: PrimaryStageListener, previousSceneListener: BackClickListener) extends DefaultBackScene(parentStage, previousSceneListener) {

  private val volumeLabel = new Label("Volume")

  private val volumeSlider = new Slider() {
    min = 0
    max = 100
    value = 50
    showTickLabels = true
    showTickMarks = true
    majorTickUnit = 50
    minorTickCount = 5
    blockIncrement = 10
    minWidth = 480
    maxWidth <== parentStage.width / 4
    // onScroll = _
  }

  private val volumeContainer = new HBox(6) {
    alignment = Pos.Center
    children = Seq(volumeLabel, volumeSlider)
  }

  private val resetGameData = new StyledButton("Reset game data") {
    // TODO: add reset game data action
    // onAction = _
  }

  /**
    * The central level container
    */
  protected val container: VBox = new VBox(10) {
    alignment = Pos.Center
    children = Seq(volumeContainer, resetGameData, goBack)
    styleClass.add("settings-vbox")
  }

  /* Setting the root container*/
  root = container
}

/**
  * Trait which gets notified when a SettingsScene event occurs
  */
trait SettingsSceneListener {

  /**
    * This method called when the level context has been created
    *
    * @param levelContext the new level context
    * @param level        the new level index
    */
  def onLevelContextCreated(levelContext: LevelContext, level: Int)
}
