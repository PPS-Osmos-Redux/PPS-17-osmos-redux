package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import scalafx.scene.control.Button
import scalafx.stage.Stage

/** An implementation of BaseScene holding a Button with let back navigation
  *
  * @param parentStage       the parent stage
  * @param backClickListener the BackClickListener
  * @param backText          the button text
  * @param additionalAction  an additional function to be executed before calling the back click listener
  */
class DefaultBackScene(override val parentStage: Stage, backClickListener: BackClickListener, backText: String = "Back", var additionalAction: () => Unit = () => {})
  extends BaseScene(parentStage) {

  /** The back button */
  protected val goBack: Button = new StyledButton(backText) {
    onAction = _ => {
      additionalAction()
      backClickListener.onBackClick()
    }
  }

  /** The text setter
    *
    * @param backText the new text that must be shown on the button
    */
  protected def setText(backText: String): Unit = {
    goBack.setText(backText)
  }

  /** The additional action setter
    *
    * @param action a new function that should be executed before calling the back listener
    */
  protected def setAdditionalAction(action: () => Unit): Unit = {
    additionalAction = action
  }
}

/** Trait used by scenes directly reachable by MainScene to notify back event */
trait BackClickListener {

  /** Called when the user wants to go back to the previous screen */
  def onBackClick()
}
