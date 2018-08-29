package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.mvc.model.MapShape
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.layout.{HBox, VBox}

/**
  * A panel showing input nodes which is also capable of providing the requested circular level
  */
class CircleLevelBuilder extends BaseComponentBuilder[MapShape.Circle] {

  /* Center */
  val x: ObjectProperty[Double] = ObjectProperty(300)
  val y: ObjectProperty[Double] = ObjectProperty(300)
  val positionNode = new VBox(2.0, new Label("Position"), new HBox(new Label("x: "), new TextField() {
    editable = false
    text <== x.asString()
  }), new HBox(new Label("y: "), new TextField() {
    editable = false
    text <== y.asString()
  }))

  /* Radius node*/
  val radius: ObjectProperty[Double] = ObjectProperty(50)
  private val radiusNode = new HBox(new Label("Radius: "), new TextField() {
    editable = false
    text <== radius.asString()
  })

  children = Seq(positionNode, radiusNode)

  override def build(): MapShape.Circle = MapShape.Circle((x.value, y.value), radius.value)
}
