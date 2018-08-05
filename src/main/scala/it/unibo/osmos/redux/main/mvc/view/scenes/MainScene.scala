package it.unibo.osmos.redux.main.mvc.view.scenes

import it.unibo.osmos.redux.main.mvc.view.menus.{MainMenuBar, MainMenuBarListener, MainMenuCenterBox, MainMenuCenterBoxListener}
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

/**
  * Opening scene, showing the menu
  */
class MainScene(override val parentStage: Stage) extends BaseScene(parentStage)
  with MainMenuCenterBoxListener with MainMenuBarListener {

  /* Requesting a structured layout */
  val rootLayout: BorderPane = new BorderPane {
    /* Setting the upper MenuBar */
    top = new MainMenuBar(MainScene.this)
    center = new MainMenuCenterBox(MainScene.this)
  }

  /* Enabling the layout */
  root = rootLayout

  override def onPlayClick(): Unit = {
    println("Play clicked")
  }

  override def onExitClick(): Unit = {
    System.exit(0)
  }

  override def onFullScreenSettingClick(): Unit = {
    parentStage.fullScreen = !parentStage.fullScreen.get()
  }
}
