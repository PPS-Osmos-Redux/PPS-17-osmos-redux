package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.mvc.model.{MediaPlayer, SoundsType}
import it.unibo.osmos.redux.mvc.view.components.menu.{MainMenuBar, MainMenuBarListener, MainMenuCenterBox, MainMenuCenterBoxListener}
import it.unibo.osmos.redux.mvc.view.containers.SettingsContainer
import scalafx.scene.Parent
import scalafx.scene.layout._
import scalafx.stage.Stage

/**
  * Opening scene, showing the menu and the menu bar
  */
class MainScene(override val parentStage: Stage, val listener: MainSceneListener) extends BaseScene(parentStage)
  with MainMenuCenterBoxListener with MainMenuBarListener with UpperMultiPlayerSceneListener with BackClickListener {

  MediaPlayer.play(SoundsType.menu)

  /* Requesting a structured layout */
  private val rootLayout: BorderPane = new BorderPane {
    /* Setting the upper MenuBar */
    top = new MainMenuBar(MainScene.this)
    center = new MainMenuCenterBox(MainScene.this)
  }
  //rootLayout.getStyleClass.add("root")
  /* Enabling the layout */
  root = rootLayout

  /*val main = new MainContainer(parentStage, listener)
  root = main.getRootLayout
  */
  override def backToMainMenu(): Unit = {
    //val main = new MainContainer(parentStage, listener)
    //root = main.getRootLayout
  }

  override def onPlayClick(): Unit = {
    //root = new LevelSelectionContainer(parentStage).getContainer
    listener.onPlayClick()
  }

  override def onMultiPlayerClick(): Unit = listener.onMultiPlayerClick()

  override def onEditorClick(): Unit = listener.onEditorClick()

  override def onSettingsClick(): Unit = {
    // TODO: this will change only the root, pressing back won't go on the correct main menu
    val v = new SettingsContainer(parentStage, listener)
    root = v.container
    //listener.onSettingsClick()
  }

  override def onExitClick(): Unit = {
    System.exit(0)
  }

  override def onFullScreenSettingClick(): Unit = {
    parentStage.fullScreen = !parentStage.fullScreen.get()
  }

  override def onMultiPlayerSceneBackClick(): Unit = {
    ActorSystemHolder.clearActors()
    parentStage.scene = this
  }

  override def onBackClick(): Unit = parentStage.scene = this
}

/**
  * Trait which gets notified when a MainScene event occurs
  */
trait MainSceneListener {

  /**
    * Called when the user clicks on the play button
    */
  def onPlayClick()

  /**
    * Called when the user clicks on the play button
    */
  def onMultiPlayerClick()

  /**
    * Called when the user clicks on the editor button
    */
  def onEditorClick()

  /**
    * Called when the user clicks on the settings button
    */
  def onSettingsClick(container: Parent)

}


