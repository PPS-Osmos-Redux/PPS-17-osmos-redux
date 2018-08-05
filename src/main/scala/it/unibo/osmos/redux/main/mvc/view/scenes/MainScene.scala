package it.unibo.osmos.redux.main.mvc.view.scenes

import it.unibo.osmos.redux.main.mvc.view.menus.{MainMenuBar, MainMenuCenterBox}
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, VBox}
import scalafx.stage.Stage

/**
  * Opening scene, showing the menu
  */
class MainScene(override val parentStage: Stage) extends BaseScene(parentStage) {

  /* Requesting a structured layout */
  val rootLayout: BorderPane = new BorderPane {
    /* Setting the upper MenuBar */
    top = new MainMenuBar()
    center = new MainMenuCenterBox()
  }

  /* Enabling the layout */
  root = rootLayout

}
