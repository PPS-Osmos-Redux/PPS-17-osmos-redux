package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.manager.sounds.MusicPlayer
import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import it.unibo.osmos.redux.mvc.view.context.LevelContext
import it.unibo.osmos.redux.mvc.view.stages.PrimaryStageListener
import javafx.scene.media.MediaPlayer.Status._
import scalafx.geometry.Pos
import scalafx.scene.control.{CheckBox, Label, Slider}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.stage.Stage

class SettingsScene(override val parentStage: Stage, listener: PrimaryStageListener, previousSceneListener: BackClickListener) extends DefaultBackScene(parentStage, previousSceneListener) {

  implicit def toDouble(number: Number): Double = number.doubleValue()

  private val volumeSlider = new Slider() {
    min = 0
    max = 100
    value = MusicPlayer.getVolume * 100
    showTickLabels = true
    showTickMarks = true
    majorTickUnit = 50
    minorTickCount = 5
    blockIncrement = 10
    minWidth = 480
    maxWidth <== parentStage.width / 4
  }
  volumeSlider.valueProperty().addListener((_, _, newVal) => {
    val valueToPlayerRange = newVal / 100
    MusicPlayer.changeVolume(valueToPlayerRange)
  })

  private val volumeLabel = new Label("Volume") {
    textFill = Color.web("#FFFFFF")
  }

  private val volumeCheckBox = new CheckBox() {
    selected = MusicPlayer.getMediaPlayerStatus match {
      case Some(PLAYING) =>
        volumeSlider.disable = false
        true
      case _ =>
        volumeSlider.disable = true
        false
    }
    onAction = _ => {
      MusicPlayer.getMediaPlayerStatus match {
        case Some(PLAYING) =>
          MusicPlayer.pause()
          volumeSlider.disable = true
        case Some(PAUSED) =>
          MusicPlayer.resume()
          volumeSlider.disable = false
        case _ =>
      }
    }
  }

  private val volumeContainer = new HBox(25) {
    alignment = Pos.Center
    children = Seq(volumeLabel, volumeCheckBox)
  }

  private val resetGameData = new StyledButton("Reset game data") {
    // TODO: add reset game data action
    // onAction = _
  }

  /**
    * The central level container
    */
  protected val container: VBox = new VBox(15) {
    alignment = Pos.Center
    children = Seq(volumeContainer, volumeSlider, resetGameData, goBack)
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
