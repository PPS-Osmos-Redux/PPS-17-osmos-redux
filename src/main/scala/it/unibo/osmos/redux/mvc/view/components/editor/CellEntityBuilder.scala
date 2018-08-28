package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.{CellBuilder, CellEntity}
import scalafx.beans.property.{DoubleProperty, ObjectProperty}
import scalafx.scene.control.TextField

/**
  * A panel showing input nodes which is also capable of providing the requested CellEntity
  */
class CellEntityBuilder extends BaseComponentBuilder[CellEntity] {

  val x: ObjectProperty[Double] = ObjectProperty(300)
  private val xTextField: TextField = new TextField() {
    text <== x.asString()
    editable = false
  }

  val y: ObjectProperty[Double] = ObjectProperty(300)
  private val yTextField: TextField = new TextField() {
    text <== y.asString()
    editable = false
  }

  var radius: DoubleProperty = DoubleProperty(300)


  children = Seq(xTextField, yTextField)





  override def build(): CellEntity = CellEntity(CellBuilder().visible(true)
  .collidable(true)
  .withPosition(x.value, y.value))

}
