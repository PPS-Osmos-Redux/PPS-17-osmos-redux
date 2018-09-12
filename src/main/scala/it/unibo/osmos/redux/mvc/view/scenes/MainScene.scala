package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.mvc.controller.manager.sounds.{MusicPlayer, SoundsType}
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures.BackgroundTexture
import it.unibo.osmos.redux.mvc.view.components.instructions.{GameInstructionScreen, GameLegendScreen}
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

/** Opening scene, showing the menu and the menu bar
  *
  * @param parentStage the parent stage
  * @param listener    the MainSceneListener
  */
class MainScene(override val parentStage: Stage, val listener: MainSceneListener) extends BaseScene(parentStage)
  with MainMenuCenterBoxListener with BackClickListener {

  MusicPlayer.play(SoundsType.menu)

  /** Background image when displaying controls */
  val background: ImageView = new ImageView(ImageLoader.getImage(BackgroundTexture)) {
    fitWidth <== parentStage.width
    fitHeight <== parentStage.height
  }

  /** Boolean binding with the legendScreen */
  private val legendScreenVisible: BooleanProperty = BooleanProperty(false)
  /** The legend screen */
  private val legendScreen = new GameLegendScreen(this).legendScreen
  legendScreen.visible <== legendScreenVisible
  /** Boolean binding with the instructionScreen */
  private val controlsScreenVisible: BooleanProperty = BooleanProperty(false)
  /** The instruction screen */
  private val instructionScreen = new GameInstructionScreen(this).instructionScreen
  /** Requesting a structured layout */
  private val rootLayout: BorderPane = new BorderPane {
    prefWidth <== parentStage.width
    prefHeight <== parentStage.height
    visible <== !controlsScreenVisible and !legendScreenVisible
    /** Setting the upper MenuBar */
    center = new MainMenuCenterBox(MainScene.this)
    bottom = new VBox(4.0, new Text("Press [C] to show/hide the game controls") {
      style = "-fx-font-size: 20pt"
      fill = Color.White
      effect = new DropShadow {
        color = Color.Blue
      }
    }, new Text("Press [L] to show/hide the game legend") {
      style = "-fx-font-size: 20pt"
      fill = Color.White
      effect = new DropShadow {
        color = Color.Blue
      }
    }
    ) {
      margin = Insets(50.0)
      alignment = Pos.Center
    }
    styleClass.add("default-background")
  }
  instructionScreen.visible <== controlsScreenVisible

  override def backToMainMenu(): Unit = {}

  override def onPlayClick(): Unit = listener.onPlayClick()

  onKeyPressed = key => key.getCode match {
    case KeyCode.C => changeControlsScreenState()
    case KeyCode.L => changeLegendScreenState()
    case _ =>
  }

  content = Seq(background, rootLayout, instructionScreen, legendScreen)

  override def onMultiPlayerClick(): Unit = listener.onMultiPlayerClick()

  override def onEditorClick(): Unit = listener.onEditorClick()

  override def onSettingsClick(): Unit = listener.onSettingsClick()

  override def onExitClick(): Unit = {
    ActorSystemHolder.kill()
    System.exit(0)
  }

  override def onBackClick(): Unit = parentStage.scene = this

  override def onStatsClick(): Unit = listener.onStatsClick()

  /** This method makes the legend screen appear/disappear */
  private def changeLegendScreenState(): Unit = {
    if (controlsScreenVisible.value) controlsScreenVisible.value = false
    legendScreenVisible.value = !legendScreenVisible.value
    background.opacity = if (legendScreenVisible.value) 0.3 else 1.0
  }

  /** This method makes the instruction screen appear/disappear */
  private def changeControlsScreenState(): Unit = {
    if (legendScreenVisible.value) legendScreenVisible.value = false
    controlsScreenVisible.value = !controlsScreenVisible.value
    background.opacity = if (controlsScreenVisible.value) 0.3 else 1.0
  }
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

  /** Called when the user clicks on the settings button */
  def onSettingsClick()

}


