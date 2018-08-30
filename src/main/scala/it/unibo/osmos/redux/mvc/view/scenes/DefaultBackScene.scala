package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import scalafx.scene.control.Button
import scalafx.stage.Stage

class DefaultBackScene(override val parentStage: Stage, previousSceneListener: BackClickListener) extends BaseScene(parentStage) {

  protected val goBack: Button = new StyledButton("Back to menu") {
    onAction = _ => previousSceneListener.onBackClick()
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
