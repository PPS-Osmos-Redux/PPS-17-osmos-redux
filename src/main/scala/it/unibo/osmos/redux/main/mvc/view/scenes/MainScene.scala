package it.unibo.osmos.redux.main.mvc.view.scenes

import it.unibo.osmos.redux.main.mvc.view.menus.{MainMenuBar, MainMenuBarListener, MainMenuCenterBox, MainMenuCenterBoxListener}
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

/**
  * Opening scene, showing the menu and the menu bar
  */
class MainScene(override val parentStage: Stage, val listener: MainSceneListener) extends BaseScene(parentStage)
  with MainMenuCenterBoxListener with MainMenuBarListener {

  /* Requesting a structured layout */
  private val rootLayout: BorderPane = new BorderPane {
    /* Setting the upper MenuBar */
    top = new MainMenuBar(MainScene.this)
    center = new MainMenuCenterBox(MainScene.this)
  }

  /* Enabling the layout */
  root = rootLayout

  override def onPlayClick(): Unit = listener.onPlayClick()

  override def onExitClick(): Unit = {
    System.exit(0)
  }

  override def onFullScreenSettingClick(): Unit = {
    parentStage.fullScreen = !parentStage.fullScreen.get()
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

}


