package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.mvc.controller.manager.sounds.{MusicPlayer, SoundsType}
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures.backgroundTexture
import it.unibo.osmos.redux.mvc.view.components.level.LevelScreen
import it.unibo.osmos.redux.mvc.view.components.menu.{MainMenuCenterBox, MainMenuCenterBoxListener}
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import javafx.scene.input.KeyCode
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.ImageView
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.stage.Stage

/** Opening scene, showing the menu and the menu bar */
class MainScene(override val parentStage: Stage, val listener: MainSceneListener) extends BaseScene(parentStage)
  with MainMenuCenterBoxListener with UpperMultiPlayerSceneListener with BackClickListener {

  MusicPlayer.play(SoundsType.menu)

  /** Background image when displaying controls */
  val background: ImageView = new ImageView(ImageLoader.getImage(backgroundTexture)) {
    fitWidth <== parentStage.width
    fitHeight <== parentStage.height
  }

  /** Boolean binding with the instructionScreen */
  private val controlsScreenVisible: BooleanProperty = BooleanProperty(false)
  /** The instruction screen */
  private val controlsScreen = LevelScreen.Builder(this)
    .withText("Game Controls", 50)
    .withText("Click on the screen to eject mass and move in the opposite direction")
    .withText("Wheel up/down to zoom in/out")
    .withText("Press [esc] to stop game (single player only)")
    .withText("Press [up] or [right] arrow key to speed up game time (single player only)")
    .withText("Press [down] or [left] arrow key to slow down game time (single player only)")
    .withText("Press [i] to show/hide the game controls", 20)
    .build()
  controlsScreen.visible <== controlsScreenVisible

  /** This method makes the instruction screen appear/disappear */
  private def changeInstructionScreenState(): Unit = {
    controlsScreenVisible.value = !controlsScreenVisible.value
    background.opacity = if (controlsScreenVisible.value) 0.3 else 1.0
  }

  /* Requesting a structured layout */
  private val rootLayout: BorderPane = new BorderPane {
    prefWidth <== parentStage.width
    prefHeight <== parentStage.height
    visible <== !controlsScreenVisible
    /* Setting the upper MenuBar */
    center = new MainMenuCenterBox(MainScene.this)
    bottom = new HBox(0.0, new Text("Press [i] to show/hide the game controls") {
      style = "-fx-font-size: 20pt"
      fill = Color.White
      effect = new DropShadow {
        color = Color.Blue
      }
    }) {
      margin = Insets(50.0)
      alignment = Pos.Center
    }
    styleClass.add("default-background")
  }

  onKeyPressed = key => key.getCode match {
    case KeyCode.I => changeInstructionScreenState()
    case _ =>
  }

  content = Seq(background, rootLayout, controlsScreen)

  override def backToMainMenu(): Unit = {}

  override def onPlayClick(): Unit = listener.onPlayClick()

  override def onMultiPlayerClick(): Unit = listener.onMultiPlayerClick()

  override def onEditorClick(): Unit = listener.onEditorClick()

  override def onSettingsClick(): Unit = listener.onSettingsClick()

  override def onExitClick(): Unit = System.exit(0)

  override def onMultiPlayerSceneBackClick(): Unit = {
    ActorSystemHolder.clearActors()
    parentStage.scene = this
  }

  override def onBackClick(): Unit = parentStage.scene = this

  override def onStatsClick(): Unit = listener.onStatsClick()

  override def onControlsClick(): Unit = listener.onControlsClick()
}

/** Trait which gets notified when a MainScene event occurs */
trait MainSceneListener {

  /** Called when the user clicks on the play button */
  def onPlayClick()

  /** Called when the user clicks on the play button */
  def onMultiPlayerClick()

  /** Called when the user clicks on the editor button */
  def onEditorClick()

  /** Called when the user clicks on the stats button */
  def onStatsClick()

  /** Called when the user clicks on the controls button */
  def onControlsClick()

  /** Called when the user clicks on the settings button */
  def onSettingsClick()

}


