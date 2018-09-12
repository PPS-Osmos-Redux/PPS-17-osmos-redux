package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.{CellBuilder, SentientCellEntity}

/** A panel showing input nodes which is also capable of providing the requested SentientCellEntity */
class SentientCellEntityCreator extends AbstractSpawnerCellEntityCreator {

  override def create(): SentientCellEntity = {
    val builder = CellBuilder()
    configureBuilder(builder)
    builder.buildSentientEntity()
  }

  override def configureBuilder(builder: CellBuilder, withEntityType: Boolean = false): Unit = {
    super.configureBuilder(builder)
    builder.withSpawner(canSpawn.value)
  }

}
