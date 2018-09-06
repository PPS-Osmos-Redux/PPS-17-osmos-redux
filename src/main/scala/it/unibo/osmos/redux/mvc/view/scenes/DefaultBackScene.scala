package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import scalafx.scene.control.Button
import scalafx.stage.Stage

class DefaultBackScene(override val parentStage: Stage, upperSceneListener: BackClickListener, backText: String = "Back",  var additionalAction: () => Unit = () => {}) extends BaseScene(parentStage) {

  protected val goBack: Button = new StyledButton(backText) {
    onAction = _ => {
      additionalAction()
      upperSceneListener.onBackClick()
    }
  }

  protected def setText(backText: String): Unit = {
    goBack.setText(backText)
  }

  protected def setAdditionalAction(action: () => Unit): Unit = {
    additionalAction = action
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
