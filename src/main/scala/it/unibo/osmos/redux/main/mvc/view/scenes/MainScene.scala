package it.unibo.osmos.redux.main.mvc.view.scenes

import it.unibo.osmos.redux.main.mvc.view.menus.{MainMenuBar, MainMenuCenterBox, MainMenuCenterBoxListener}
import scalafx.scene.layout.BorderPane
import scalafx.stage.Stage

/**
  * Opening scene, showing the menu
  */
class MainScene(override val parentStage: Stage) extends BaseScene(parentStage)
  with MainMenuCenterBoxListener {

  /* Requesting a structured layout */
  val rootLayout: BorderPane = new BorderPane {
    /* Setting the upper MenuBar */
    top = new MainMenuBar()
    center = new MainMenuCenterBox(MainScene.this)
  }

  /* Enabling the layout */
  root = rootLayout

  /**
    * Called when the user click on the play button
    */
  override def onPlayClick(): Unit = {
    println("Play clicked")
  }

  /**
    * Called when the user click on the exit button
    */
  override def onExitClick(): Unit = {
    println("Exit clicked")
  }
}
