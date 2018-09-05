package it.unibo.osmos.redux.ecs.entities.builders

import it.unibo.osmos.redux.ecs.entities.CellEntity

/** Builder for Cell Entities */
case class CellBuilder() extends EntityBuilder[CellEntity] {

  override def build: CellEntity = {
    buildBaseCell()
  }
}
