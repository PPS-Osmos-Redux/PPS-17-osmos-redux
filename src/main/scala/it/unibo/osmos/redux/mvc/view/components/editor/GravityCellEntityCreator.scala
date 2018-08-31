package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.builders.{CellBuilder, GravityCellBuilder}
import it.unibo.osmos.redux.ecs.entities.{CellEntity, GravityCellEntity}
import it.unibo.osmos.redux.mvc.view.components.custom.TitledDoubleField
import scalafx.beans.property.DoubleProperty
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox

/**
  * A panel showing input nodes which is also capable of providing the requested GravityCellEntity
  *
  * @param isAttractive true if the gravity cell is an attractive one, false if it is repulsive
  */
class GravityCellEntityCreator(var isAttractive: Boolean) extends CellEntityCreator {

  /* Specific weight node*/
  val weight: DoubleProperty = DoubleProperty(0.0)
  val weightNode = new VBox(2.0, new Label("Weight"), new TitledDoubleField("Weight: ", weight).innerNode)

  children.add(weightNode)

  override def configureBuilder(builder: CellBuilder): Unit = {
    builder match {
      case gcb: GravityCellBuilder =>
        super.configureBuilder(gcb)
        gcb.withSpecificWeight(weight.value)
      case _ => throw new IllegalArgumentException("GravityCellEntityCreator must use a GravityCellBuilder")
    }
  }

  override def create(): CellEntity = {
    val builder = GravityCellBuilder()
    configureBuilder(builder)
    builder.build
  }

}
