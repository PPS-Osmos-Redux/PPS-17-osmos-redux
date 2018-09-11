package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.mvc.view.components.custom.TitledComboBox
import scalafx.beans.property.BooleanProperty

/**
  * Abstract Creator for Spawner entities
  */
abstract class AbstractSpawnerCellEntityCreator extends CellEntityCreator {

  /** canSpawn property combo box */
  protected val canSpawn: BooleanProperty = BooleanProperty(true)
  protected val canSpawnComboBox = new TitledComboBox[Boolean]("Can spawn", Seq(true, false), (b) => {canSpawn.value = b}, vertical = false)

  children.add(canSpawnComboBox.root)

}
