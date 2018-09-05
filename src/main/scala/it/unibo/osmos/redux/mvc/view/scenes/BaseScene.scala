package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.manager.files.FileManager
import scalafx.scene.Scene
import scalafx.stage.Stage

/**
  * BaseScene case class which holds the reference to the parent Stage instance
  */
case class BaseScene(parentStage: Stage) extends Scene {

  // TODO: styles are loaded each time a scene changes, should be loaded one time only
  this.getStylesheets.addAll(FileManager.getStyle)
}
