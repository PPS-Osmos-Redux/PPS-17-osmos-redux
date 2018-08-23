package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.components.editor.{EditorLevelNode, EditorLevelNodeListener}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.stage.Stage

class EditorLevelSelectionScene(override val parentStage: Stage, override val listener: LevelSelectionSceneListener) extends LevelSelectionScene(parentStage, listener)
  with EditorLevelNodeListener with EditorSceneListener {

  private val newLevelButton = new Button("Create new level") {
    margin = Insets(0, 0, 20, 0)
    alignment = Pos.BottomCenter
    alignmentInParent = Pos.BottomCenter
    /* We open the editor */
    onAction = _ => parentStage.scene = new EditorScene(parentStage, EditorLevelSelectionScene.this)
  }

  container.children.add(newLevelButton)

  //TODO: edit proper value
  override def numLevels: Int = 5

  override def loadLevels(): Unit = for (i <- 1 to numLevels) levelsContainer.children.add(new EditorLevelNode(this, i))

  override def onLevelDeleteClick(level: Int): Unit = {}


}
