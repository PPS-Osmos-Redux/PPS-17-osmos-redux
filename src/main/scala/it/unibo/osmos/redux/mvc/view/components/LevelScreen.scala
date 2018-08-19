package it.unibo.osmos.redux.mvc.view.components

import scalafx.geometry.Pos
import scalafx.scene.control.Button
import scalafx.scene.{Node, Scene}
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, Text}

import scala.collection.mutable

/**
  * A simple splash screen shown at the beginning of the level
  */
object LevelScreen {

  case class Builder(private val parentScene: Scene) {

    private var components: mutable.ListBuffer[Node] = mutable.ListBuffer()

    def withText(text: String, size: Double, color: Color): Builder = {
      components += new Text(text) {
        font = Font.font("Verdana", size)
        fill = color
      }
      this
    }

    def withButton(text: String, onClick: => Unit): Builder = {
      components += new Button(text) {
        onAction = _ => onClick
      }
      this
    }

    def build() : LevelScreenImpl = new LevelScreenImpl(parentScene, components)
  }

  protected class LevelScreenImpl(parentScene: Scene, components: Seq[Node]) extends VBox {
    prefWidth <== parentScene.width
    prefHeight <== parentScene.height
    alignment = Pos.Center
    parentScene fill = Color.Black

    children = components

  }


}


