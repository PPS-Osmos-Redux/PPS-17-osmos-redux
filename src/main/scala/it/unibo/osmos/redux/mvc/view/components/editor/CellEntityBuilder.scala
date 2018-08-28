package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.{CellBuilder, CellEntity}
import it.unibo.osmos.redux.mvc.view.components.custom.TitledDoubleField
import scalafx.beans.property.{DoubleProperty, ObjectProperty}
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.layout.{HBox, VBox}

/**
  * A panel showing input nodes which is also capable of providing the requested CellEntity
  */
class CellEntityBuilder extends BaseComponentBuilder[CellEntity] {

  /* Position node */
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
  val radius: ObjectProperty[Double] = ObjectProperty(150)
  private val radiusNode = new HBox(new Label("radius: "), new TextField() {
    editable = false
    text <== radius.asString()
  })

  /* Position node */
  val xSpeed: DoubleProperty = DoubleProperty(0.0)
  val ySpeed: DoubleProperty = DoubleProperty(0.0)
  val speedNode = new VBox(2.0, new Label("Speed"),
    new TitledDoubleField("x: ", xSpeed).innerNode,
    new TitledDoubleField("y: ", ySpeed).innerNode
  )

  children = Seq(positionNode, radiusNode, speedNode)

  override def build(): CellEntity = CellEntity(CellBuilder().visible(true)
  .collidable(true)
  .withPosition(x.value, y.value))

}
