package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.{CellBuilder, PlayerCellEntity}

/**
  * A panel showing input nodes which is also capable of providing the requested PlayerCellEntity
  */
class PlayerCellEntityCreator extends AbstractSpawnerCellEntityCreator {

  override def configureBuilder(builder: CellBuilder, withEntityType: Boolean = true): Unit = {
    super.configureBuilder(builder)
    builder.withSpawner(canSpawn.value)
  }

  override def create(): PlayerCellEntity = {
    val builder = CellBuilder()
    configureBuilder(builder)
    builder.buildPlayerEntity()
  }
}
