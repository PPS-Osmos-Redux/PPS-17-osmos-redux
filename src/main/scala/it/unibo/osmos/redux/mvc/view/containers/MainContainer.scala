package it.unibo.osmos.redux.mvc.view.containers

import it.unibo.osmos.redux.mvc.view.scenes.MainSceneListener
import javafx.scene.Parent
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.stage.Stage

class MainContainer(parentStage: Stage, val listener: MainSceneListener) {

  var root: ObjectProperty[Parent] = _

  def setRoot(root: ObjectProperty[Parent]): Unit = {
    this.root = root
  }

  private val fullscreen: Button = new Button("Fullscreen") {
    onAction = _ => parentStage.fullScreen = !parentStage.fullScreen.get()
  }

  private val play: Button = new Button("Play") {
    onAction = _ => ???
  }

  private val settings: Button = new Button("Settings") {
    onAction = _ => {
      val container = new SettingsContainer(parentStage, listener)
      listener.onSettingsClick(container.container)
    }
  }

  private val exit: Button = new Button("Exit") {
    onAction = _ => System.exit(0)
  }

  var rootLayout: VBox = new VBox {
    alignment = Pos.Center
    children = Seq(fullscreen, play, settings, exit)
  }

  def getRootLayout: VBox = rootLayout

  /* Enabling the layout */
  //root = rootLayout

  //override def onPlayClick(): Unit = listener.onPlayClick()

  //override def onMultiPlayerClick(): Unit = listener.onMultiPlayerClick()

  //override def onEditorClick(): Unit = listener.onEditorClick()

  /*override def onSettingsClick(): Unit = {
    val v = new SettingsContainer(parentStage)
    //root = v.container
    //listener.onSettingsClick()
  }*/
}
