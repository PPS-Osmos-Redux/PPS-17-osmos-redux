package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.scene.control.Button

class StyledButton(title: String) extends Button(title) {

  //textAlignment = TextAlignment.Center
  this.getStyleClass.addAll("default-button-style", "enabled-button-style")

}
