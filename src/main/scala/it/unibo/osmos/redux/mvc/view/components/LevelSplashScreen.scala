package it.unibo.osmos.redux.mvc.view.components

import scalafx.geometry.Pos
import scalafx.scene.Scene
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, Text}

/**
  * A simple splash screen shown at the beginning of the level
  */
class LevelSplashScreen(val parentScene: Scene, val text: String, val textSize: Double) extends VBox {
  prefWidth <== parentScene.width
  prefHeight <== parentScene.height
  alignment = Pos.Center
  parentScene fill = Color.Black

  children = Seq(new Text(text) {
    font = Font.font("Verdana", textSize)
    fill = Color.White
  })
}
