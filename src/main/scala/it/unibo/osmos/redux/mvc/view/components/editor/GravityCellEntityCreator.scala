package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.{CellBuilder, GravityCellEntity}
import it.unibo.osmos.redux.mvc.view.components.custom.TitledDoubleField
import scalafx.beans.property.DoubleProperty
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox

/** A panel showing input nodes which is also capable of providing the requested GravityCellEntity */
class GravityCellEntityCreator extends CellEntityCreator {

  /** Specific weight node */
  val weight: DoubleProperty = DoubleProperty(1.0)
  val weightNode = new VBox(2.0, new Label("Weight"), new TitledDoubleField("Weight: ", weight, 0.0, Double.MaxValue).innerNode)

  children.add(weightNode)

  override def create(): GravityCellEntity = {
    val builder = CellBuilder()
    configureBuilder(builder)
    builder.buildGravityEntity()
  }

  override def configureBuilder(builder: CellBuilder, withEntityType: Boolean = true): Unit = {
    super.configureBuilder(builder)
    builder.withSpecificWeight(weight.value)
  }
}
