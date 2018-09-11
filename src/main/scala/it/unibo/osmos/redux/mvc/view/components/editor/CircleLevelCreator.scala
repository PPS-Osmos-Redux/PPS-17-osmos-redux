package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.mvc.controller.levels.structure.MapShape
import it.unibo.osmos.redux.mvc.view.ViewConstants
import it.unibo.osmos.redux.mvc.view.components.custom.TitledDoubleField
import it.unibo.osmos.redux.utils.Point
import scalafx.beans.property.DoubleProperty
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox

/**
  * A panel showing input nodes which is also capable of providing the requested circular level
  */
class CircleLevelCreator extends BaseEditorCreator[MapShape.Circle] {

  /** Center */
  val xCenter: DoubleProperty = DoubleProperty(0.0)
  val yCenter: DoubleProperty = DoubleProperty(0.0)
  private val centerNode = new VBox(2.0, new Label("Center"),
    new TitledDoubleField("x: ", xCenter).innerNode,
    new TitledDoubleField("y: ", yCenter).innerNode
  )

  /** Radius node*/
  val radius: DoubleProperty = DoubleProperty(0.0)
  val maxRadius: Double = ViewConstants.Editor.MaxLevelRadius
  private val radiusNode = new VBox(2.0, new Label(s"Radius (max: $maxRadius)"),
    new TitledDoubleField("radius: ", radius, 1.0, maxRadius).innerNode,
  )

  children = Seq(centerNode, radiusNode)

  override def create(): MapShape.Circle = MapShape.Circle(Point(xCenter.value, yCenter.value), radius.value)
}
