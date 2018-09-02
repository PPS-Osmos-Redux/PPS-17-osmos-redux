package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.components.custom.TitledDoubleField
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
  ) {
    xCenter.value = 400.0
    yCenter.value = 400.0
  }

  /* Level width node*/
  val levelWidth: DoubleProperty = DoubleProperty(0.0)
  private val widthNode = new VBox(2.0, new Label("Width"),
    new TitledDoubleField("Width: ", levelWidth).innerNode,
  ) {
    levelWidth.value = 300.0
  }

  /* Level height node*/
  val levelHeight: DoubleProperty = DoubleProperty(0.0)
  private val heightNode = new VBox(2.0, new Label("Height"),
    new TitledDoubleField("Height: ", levelHeight).innerNode,
  ) {
    levelHeight.value = 300.0
  }

  children = Seq(centerNode, widthNode, heightNode)

  override def create(): MapShape.Rectangle = MapShape.Rectangle((xCenter.value, yCenter.value), levelHeight.value, levelWidth.value)
}
