package it.unibo.osmos.redux.mvc.view.scenes

import scalafx.scene.Scene
import scalafx.stage.Stage

/** BaseScene case class which holds the reference to the parent Stage instance */
case class BaseScene(parentStage: Stage) extends Scene {


  protected def setBackground(): Unit = {
    content = Seq()
  }
}


