package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.scene.control.Button

/** Button with the application style
  *
  * @param text the button text
  */
class StyledButton(text: String) extends Button(text) {

  /** Loading the style */
  this.getStyleClass.addAll("default-button-style", "enabled-button-style")

}
