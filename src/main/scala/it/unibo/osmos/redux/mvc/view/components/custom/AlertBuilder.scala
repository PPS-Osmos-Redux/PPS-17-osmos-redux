package it.unibo.osmos.redux.mvc.view.components.custom

import it.unibo.osmos.redux.mvc.controller.FileManager
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

class AlertBuilder {

  private var alertType = AlertType.Error
  private var alertTitle = "Generic Alert Dialog"
  private var alertHeaderText: String = _
  private var alertGraphic: Boolean = false
  private var alertContentText = "Unknown Error"

  def setType(alertType: AlertType): AlertBuilder = {
    this.alertType = alertType
    this
  }

  def setTitle(alertTitle: String): AlertBuilder = {
    this.alertTitle = alertTitle
    this
  }

  def setHeaderText(alertHeaderText: String): AlertBuilder = {
    this.alertHeaderText = alertHeaderText
    this
  }

  def setGraphics(enable: Boolean): AlertBuilder = {
    alertGraphic = enable
    this
  }

  def setAlertContentText(alertContentText: String): AlertBuilder = {
    this.alertContentText = alertContentText
    this
  }

  def build(): Alert = {
    val alert = new Alert(alertType) {
      title = alertTitle
      headerText = alertHeaderText
      if (!alertGraphic) {
        graphic = null
      }
      contentText = alertContentText
    }
    alert.getDialogPane.getStylesheets.addAll(FileManager.getStyle)
    alert.getDialogPane.getStyleClass.add("dialog-style")
    //alert.getDialogPane.setStyle("-fx-font: 20 arial;")
    alert
  }

}
