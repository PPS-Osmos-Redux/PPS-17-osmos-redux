package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.layout.{HBox, Pane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

abstract class TitledNode[N <: Node](val title: String, vertical: Boolean, spacing: Double = 4.0) {

  val root: Pane = if (vertical) new VBox(spacing) else new HBox(spacing)
  root.padding = Insets(10.0)
  root.style = "-fx-background-color : #ffffff;"

  private val text = new Text(title) {
    fill = Color.Black
  }

  def node: N

  root.children = List(text, node)

}
