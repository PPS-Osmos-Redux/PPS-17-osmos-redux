package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.CellEntity
import it.unibo.osmos.redux.ecs.entities.builders.{CellBuilder, SentientCellBuilder}
import it.unibo.osmos.redux.mvc.view.components.custom.TitledComboBox
import scalafx.beans.property.BooleanProperty

/**
  * A panel showing input nodes which is also capable of providing the requested SentientCellEntity
  */
class SentientCellEntityCreator extends CellEntityCreator {

  /* Can spawn combo box */
  private val canSpawn: BooleanProperty = BooleanProperty(true)
  val canSpawnComboBox = new TitledComboBox[Boolean]("Can spawn", Seq(true, false), (b) => {canSpawn.value = b}, vertical = false)

  children.add(canSpawnComboBox.root)

  override def configureBuilder(builder: CellBuilder, withEntityType: Boolean = false): Unit = {
    builder match {
      case scb: SentientCellBuilder =>
        super.configureBuilder(scb, withEntityType = false)
        scb.withSpawner(canSpawn.value)
      case _ => throw new IllegalArgumentException("SentientCellEntityCreator must use a SentientCellBuilder")
    }
  }

  override def create(): CellEntity = {
    val builder = SentientCellBuilder()
    configureBuilder(builder)
    builder.build
  }

}
