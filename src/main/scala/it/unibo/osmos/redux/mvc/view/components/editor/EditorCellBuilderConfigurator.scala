package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.builders.CellBuilder

/**
  * Basic CellBuilder configurator, used by the various cell editors
  * @tparam T a subtype of CellBuilder
  */
trait EditorCellBuilderConfigurator[T <: CellBuilder] {

  /**
    * This method configure the CellBuilder
    * @param builder the CellBuilder that must be configured
    */
  def configureBuilder(builder: T): Unit

}
