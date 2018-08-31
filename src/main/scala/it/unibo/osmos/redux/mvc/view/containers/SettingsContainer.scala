package it.unibo.osmos.redux.mvc.view.containers

import it.unibo.osmos.redux.mvc.view.scenes.MainSceneListener
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, CheckBox}
import scalafx.scene.layout.VBox
import scalafx.stage.Stage

class SettingsContainer(parentStage: Stage, listener: MainSceneListener) {

  private val fullScreen = new CheckBox("Fullscreen") {
    onAction = _ => parentStage.fullScreen = !parentStage.fullScreen.get()
  }

  fullScreen.selected = parentStage.fullScreen.get()

  private val goBack = new Button("Back") {
    onAction = _ => {
      val main = new MainContainer(parentStage, listener)
      listener.onSettingsClick(main.getRootLayout)
    }
  }

  /**
    * The central level container
    */
  val container: VBox = new VBox {
    alignment = Pos.Center
    children = Seq(fullScreen, goBack)
  }
}
