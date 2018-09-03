package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType}

object AlertFactory {

  def createConfirmationAlert(title: String, message: String): Alert = {
    new AlertBuilder().setType(AlertType.Confirmation)
      .setTitle(title)
      .setAlertContentText(message)
      .build()
  }

  def showConfirmationAlert(title: String, message: String, positiveAction: => Unit, negativeAction: => Unit): Unit = {
    val buttonType = createConfirmationAlert(title, message).showAndWait()
    buttonType match {
      case Some(bType) if bType == ButtonType.OK => positiveAction
      case _ => negativeAction
    }
  }

  def createErrorAlert(title: String, message: String): Alert = {
    new AlertBuilder().setTitle(title)
      .setAlertContentText(message)
      .build()
  }
}
