package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.multiplayer.common.ActorSystemHolder
import it.unibo.osmos.redux.mvc.view.components.menu.{MainMenuBar, MainMenuBarListener, MainMenuCenterBox, MainMenuCenterBoxListener}
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

/**
  * Opening scene, showing the menu and the menu bar
  */
class MainScene(override val parentStage: Stage, val listener: MainSceneListener) extends BaseScene(parentStage)
  with MainMenuCenterBoxListener with MainMenuBarListener with UpperMultiPlayerSceneListener {

  /* Requesting a structured layout */
  private val rootLayout: BorderPane = new BorderPane {
    /* Setting the upper MenuBar */
    top = new MainMenuBar(MainScene.this)
    center = new MainMenuCenterBox(MainScene.this)
  }

  /* Enabling the layout */
  root = rootLayout

  override def onPlayClick(): Unit = listener.onPlayClick()

  override def onMultiPlayerClick(): Unit = listener.onMultiPlayerClick()

  override def onEditorClick(): Unit = listener.onEditorClick()

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

}


