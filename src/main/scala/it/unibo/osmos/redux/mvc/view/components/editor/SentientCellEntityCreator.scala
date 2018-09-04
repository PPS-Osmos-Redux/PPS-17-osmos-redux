package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.CellEntity
import it.unibo.osmos.redux.ecs.entities.builders.{CellBuilder, SentientCellBuilder}

/**
  * A panel showing input nodes which is also capable of providing the requested SentientCellEntity
  */
class SentientCellEntityCreator extends AbstractSpawnerCellEntityCreator {

  override def configureBuilder(builder: CellBuilder, withEntityType: Boolean = false): Unit = {
    builder match {
      case scb: SentientCellBuilder =>
        super.configureBuilder(scb)
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
