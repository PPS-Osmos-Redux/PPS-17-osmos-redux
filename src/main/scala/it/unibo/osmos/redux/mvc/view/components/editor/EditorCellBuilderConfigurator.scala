package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.CellBuilder

/**
  * Basic CellBuilder configurator, used by the various cell editors
  */
trait EditorCellBuilderConfigurator {

  /** This method configure the EntityBuilder
    *
    * @param builder the CellBuilder that must be configured
    * @param withEntityType true if the Configurator has to put the entity type in the newly created cell
    */
  def configureBuilder(builder: CellBuilder, withEntityType: Boolean = true): Unit

}
