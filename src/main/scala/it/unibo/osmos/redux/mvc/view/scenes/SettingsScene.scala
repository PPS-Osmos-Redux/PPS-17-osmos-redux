package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.SettingsHolder
import it.unibo.osmos.redux.mvc.controller.levels.SinglePlayerLevels
import it.unibo.osmos.redux.mvc.controller.manager.sounds.MusicPlayer
import it.unibo.osmos.redux.mvc.view.components.custom.{AlertFactory, StyledButton}
import javafx.scene.media.MediaPlayer.Status._
import scalafx.geometry.Pos
import scalafx.scene.control.{CheckBox, Label, Slider}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.stage.Stage

/** Scene where the user can configure in game settings
  *
  * @param parentStage the parent stage
  * @param listener the SettingsSceneListener
  * @param previousSceneListener the BackClickListener
  */
class SettingsScene(override val parentStage: Stage, listener: SettingsSceneListener, previousSceneListener: BackClickListener) extends DefaultBackScene(parentStage, previousSceneListener) {

  private implicit def toDouble(number: Number): Double = number.doubleValue()

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
    onAction = _ => AlertFactory.showConfirmationAlert(text.value + "?", "Your progresses will be lost", SinglePlayerLevels.reset(), {})
  }

  /** adds save settings to goBack button */
  setAdditionalAction(() => SettingsHolder.saveSettings())

  /**  The central container */
  protected val container: VBox = new VBox(15) {
    alignment = Pos.Center
    children = Seq(volumeContainer, volumeSlider, resetGameData, goBack)
    styleClass.add("settings-vbox")
  }

  /* Setting the root container */
  root = container
}

/** Trait which gets notified when a SettingsScene event occurs */
trait SettingsSceneListener {


}
