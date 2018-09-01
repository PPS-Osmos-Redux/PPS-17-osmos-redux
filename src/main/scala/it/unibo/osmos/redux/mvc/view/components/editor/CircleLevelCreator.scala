package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.components.custom.TitledDoubleField
import scalafx.beans.property.{DoubleProperty, ObjectProperty}
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.layout.{HBox, VBox}

/**
  * A panel showing input nodes which is also capable of providing the requested circular level
  */
class CircleLevelCreator extends BaseEditorCreator[MapShape.Circle] {

  /* Center */
  val xCenter: DoubleProperty = DoubleProperty(0.0)
  val yCenter: DoubleProperty = DoubleProperty(0.0)
  private val centerNode = new VBox(2.0, new Label("Center"),
    new TitledDoubleField("x: ", xCenter).innerNode,
    new TitledDoubleField("y: ", yCenter).innerNode
  )

  /* Radius node*/
  val radius: DoubleProperty = DoubleProperty(0.0)
  private val radiusNode = new VBox(2.0, new Label("Radius"),
    new TitledDoubleField("radius: ", radius).innerNode,
  )

  children = Seq(centerNode, radiusNode)

  override def create(): MapShape.Circle = MapShape.Circle((xCenter.value, yCenter.value), radius.value)
}
