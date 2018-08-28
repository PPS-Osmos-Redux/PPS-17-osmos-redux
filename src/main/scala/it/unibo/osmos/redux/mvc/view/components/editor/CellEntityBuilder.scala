package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.{CellBuilder, CellEntity}
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.{Label, TextField}
import scalafx.scene.layout.HBox

/**
  * A panel showing input nodes which is also capable of providing the requested CellEntity
  */
class CellEntityBuilder extends BaseComponentBuilder[CellEntity] {

  val x: ObjectProperty[Double] = ObjectProperty(300)
  private val xNode = new HBox(new Label("x: "), new TextField() {
    editable = false
    text <== x.asString()
  })

  val y: ObjectProperty[Double] = ObjectProperty(300)
  private val yNode = new HBox(new Label("y: "), new TextField() {
    editable = false
    text <== y.asString()
  })

  val radius: ObjectProperty[Double] = ObjectProperty(150)
  private val radiusNode = new HBox(new Label("radius: "), new TextField() {
    editable = false
    text <== radius.asString()
  })

  children = Seq(xNode, yNode, radiusNode)

  override def build(): CellEntity = CellEntity(CellBuilder().visible(true)
  .collidable(true)
  .withPosition(x.value, y.value))

}
