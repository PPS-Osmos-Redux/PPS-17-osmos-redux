package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.CellEntity
import it.unibo.osmos.redux.ecs.entities.builders.{EntityBuilder, GravityCellBuilder}
import it.unibo.osmos.redux.mvc.view.components.custom.TitledDoubleField
import scalafx.beans.property.DoubleProperty
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox

/**
  * A panel showing input nodes which is also capable of providing the requested GravityCellEntity
  *
  */
class GravityCellEntityCreator extends CellEntityCreator {

  /* Specific weight node*/
  val weight: DoubleProperty = DoubleProperty(1.0)
  val weightNode = new VBox(2.0, new Label("Weight"), new TitledDoubleField("Weight: ", weight, 0.0, Double.MaxValue).innerNode)

  children.add(weightNode)

  override def configureBuilder[T](builder: EntityBuilder[T], withEntityType: Boolean = true): Unit = {
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
