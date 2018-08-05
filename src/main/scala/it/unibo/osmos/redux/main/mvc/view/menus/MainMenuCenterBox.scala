package it.unibo.osmos.redux.main.mvc.view.menus

import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox

/**
  * Center menu shown in MainMenu
  */
class MainMenuCenterBox extends VBox {

  alignment = Pos.Center
  spacing = 10

  /* Play button */
  val playButton = new Button("Play")
  /* Exit button */
  val exitButton = new Button("Exit")

  children = List(playButton, exitButton)


}
