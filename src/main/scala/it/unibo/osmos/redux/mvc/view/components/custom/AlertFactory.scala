package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

object AlertFactory {

  def createConfirmationAlert(title: String, message: String): Alert = {
    new AlertBuilder().setType(AlertType.Confirmation)
      .setTitle(title)
      .setAlertContentText(message)
      .build()
  }

  def createErrorAlert(title: String, message: String): Alert = {
    new AlertBuilder().setTitle(title)
      .setAlertContentText(message)
      .build()
  }
}
