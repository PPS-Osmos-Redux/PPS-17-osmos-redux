package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.components.custom.TitledDoubleField
import it.unibo.osmos.redux.utils.Point
import scalafx.beans.property.DoubleProperty
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox

/**
  * A panel showing input nodes which is also capable of providing the requested rectangular level
  */
class RectangleLevelCreator extends BaseEditorCreator[MapShape.Rectangle] {

  /* Center */
  val xCenter: DoubleProperty = DoubleProperty(0.0)
  val yCenter: DoubleProperty = DoubleProperty(0.0)
  private val centerNode = new VBox(2.0, new Label("Center"),
    new TitledDoubleField("x: ", xCenter).innerNode,
    new TitledDoubleField("y: ", yCenter).innerNode
  )

  /* Level width node*/
  val levelWidth: DoubleProperty = DoubleProperty(0.0)
  private val widthNode = new VBox(2.0, new Label("Width"),
    new TitledDoubleField("Width: ", levelWidth, 1.0, Double.MaxValue).innerNode,
  )

  /* Level height node*/
  val levelHeight: DoubleProperty = DoubleProperty(0.0)
  private val heightNode = new VBox(2.0, new Label("Height"),
    new TitledDoubleField("Height: ", levelHeight, 1.0, Double.MaxValue).innerNode,
  )

  children = Seq(centerNode, widthNode, heightNode)

  override def create(): MapShape.Rectangle = MapShape.Rectangle(Point(xCenter.value, yCenter.value), levelHeight.value, levelWidth.value)
}
