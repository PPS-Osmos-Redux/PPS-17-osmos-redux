package it.unibo.osmos.redux.mvc.view.components.custom

import it.unibo.osmos.redux.mvc.controller.manager.files.StyleFileManager
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.layout.Region

/**
  * A builder that provider useful method to create customizable alerts
  */
class AlertBuilder {

  private var alertType = AlertType.Error
  private var alertTitle = "Generic Alert Dialog"
  private var alertHeaderText: String = _
  private var alertGraphic: Boolean = false
  private var alertContentText = "Unknown Error"

  /** This method sets the alert type
    *
    * @param alertType the alert type
    * @return the builder itself
    */
  def setType(alertType: AlertType): AlertBuilder = {
    this.alertType = alertType
    this
  }

  /** This method sets the alert title
    *
    * @param alertTitle the alert title
    * @return the builder itself
    */
  def setTitle(alertTitle: String): AlertBuilder = {
    this.alertTitle = alertTitle
    this
  }

  /** This method sets the alert header
    *
    * @param alertHeaderText the header text
    * @return the builder itself
    */
  def setHeaderText(alertHeaderText: String): AlertBuilder = {
    this.alertHeaderText = alertHeaderText
    this
  }

  /** This method sets the graphics flag
    *
    * @param enable true if the alert graphics are enabled, false otherwise
    * @return the builder itself
    */
  def setGraphics(enable: Boolean): AlertBuilder = {
    alertGraphic = enable
    this
  }

  /** This method sets the alert content
    *
    * @param alertContentText the content text
    * @return the builder itself
    */
  def setAlertContentText(alertContentText: String): AlertBuilder = {
    this.alertContentText = alertContentText
    this
  }

  /** This method build the requested alert
    *
    * @return an alert
    */
  def build(): Alert = {
    val alert = new Alert(alertType) {
      title = alertTitle
      headerText = None
      if (!alertGraphic) {
        graphic = null
      }
      contentText = alertContentText
    }
    alert.getDialogPane.setMinHeight(Region.USE_PREF_SIZE)
    alert.getDialogPane.getStylesheets.addAll(StyleFileManager.getStyle)
    alert.getDialogPane.getStyleClass.add("dialog-style")
    alert
  }

}
