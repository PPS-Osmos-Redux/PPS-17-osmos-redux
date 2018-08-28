package it.unibo.osmos.redux.mvc.view.scenes

import scalafx.scene.control.Button
import scalafx.stage.Stage

class DefaultBackScene(override val parentStage: Stage, backClickListener: BackClickListener) extends BaseScene(parentStage) {

  protected val goBack: Button = new Button("Back to menù") {
    onAction = _ => backClickListener.onBackClick()
  }
}

/**
  * Trait used by scenes directly reachable by MainScene to notify back event
  */
trait BackClickListener {

  /**
    * Called when the user wants to go back to the previous screen
    */
  def onBackClick()
}
