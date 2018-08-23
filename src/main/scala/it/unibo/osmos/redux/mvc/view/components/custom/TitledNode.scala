package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.layout.{HBox, Pane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

/**
  * Abstract node which holds a Text and a generic node subclass in a Pane
  * @param title the text shown
  * @param vertical true if the text must be shown above the node, false otherwise
  * @param spacing the spacing between the text and the node
  * @tparam N the node type
  */
abstract class TitledNode[N <: Node](val title: String, vertical: Boolean, spacing: Double = 4.0) {

  val root: Pane = if (vertical) new VBox(spacing) else new HBox(spacing)
  root.padding = Insets(10.0)
  root.style = "-fx-background-color : #ffffff;"

  private val text = new Text(title) {
    fill = Color.Black
  }

  /**
    * The node that will be shown after the text
    * @return a node of type N <: Node
    */
  def node: N

  root.children = List(text, node)

}
