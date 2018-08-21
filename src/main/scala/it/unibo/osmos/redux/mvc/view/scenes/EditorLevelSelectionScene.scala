package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.components.{EditorLevelNode, EditorLevelNodeListener, LevelNode}
import scalafx.stage.Stage

class EditorLevelSelectionScene(override val parentStage: Stage, override val listener: LevelSelectionSceneListener) extends LevelSelectionScene(parentStage, listener)
  with EditorLevelNodeListener{

  //TODO: edit proper value
  override def numLevels: Int = 5

  override def loadLevels(): Unit = for (i <- 1 to numLevels) levelsContainer.children.add(new EditorLevelNode(this, i))

  override def onLevelDeleteClick(level: Int): Unit = {}

}
