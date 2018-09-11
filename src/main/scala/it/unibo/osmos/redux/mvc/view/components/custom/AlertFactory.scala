package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType}

/**
  * A factory providing useful method concerning alert implementation
  */
object AlertFactory {

  /** This method creates a simple confirmation alert
    *
    * @param title the alert title
    * @param message the alert message
    * @return a confirmation alert
    */
  def createConfirmationAlert(title: String, message: String): Alert = {
    new AlertBuilder().setType(AlertType.Confirmation)
      .setTitle(title)
      .setAlertContentText(message)
      .build()
  }

  /** This method shows a confirmation alert and executes the provided functions according to the user response
    *
    * @param title the alert title
    * @param message the alert message
    * @param positiveAction the function called when the user presses on the positive button
    * @param negativeAction the function called when the user presses on the negative button
    */
  def showConfirmationAlert(title: String, message: String, positiveAction: => Unit, negativeAction: => Unit): Unit = {
    val buttonType = createConfirmationAlert(title, message).showAndWait()
    buttonType match {
      case Some(bType) if bType == ButtonType.OK => positiveAction
      case _ => negativeAction
    }
  }

  /** This method creates a simple error alert
    *
    * @param title the alert title
    * @param message the alert message
    * @return an error alert
    */
  def createErrorAlert(title: String, message: String): Alert = {
    new AlertBuilder().setTitle(title)
      .setAlertContentText(message)
      .build()
  }
}
