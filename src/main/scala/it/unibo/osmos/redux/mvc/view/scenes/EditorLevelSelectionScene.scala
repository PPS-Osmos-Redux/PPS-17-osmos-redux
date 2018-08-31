package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.model.SinglePlayerLevels.LevelInfo
import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import it.unibo.osmos.redux.mvc.view.components.editor.{EditorLevelNode, EditorLevelNodeListener}
import scalafx.geometry.{Insets, Pos}
import scalafx.stage.Stage

class EditorLevelSelectionScene(override val parentStage: Stage, override val listener: EditorLevelSelectionSceneListener) extends LevelSelectionScene(parentStage, listener)
  with EditorLevelNodeListener {

  private val newLevelButton = new StyledButton("Create new level") {
    margin = Insets(0, 0, 20, 0)
    alignment = Pos.BottomCenter
    alignmentInParent = Pos.BottomCenter
    /* We open the editor. On level saved we recreate the scene */
    onAction = _ => parentStage.scene = new EditorScene(parentStage, listener, () => parentStage.scene = new EditorLevelSelectionScene(parentStage, listener))
  }

  container.children.add(newLevelButton)

  /** Custom levels are always available */
  override def levels: List[LevelInfo] = listener.getCustomLevels
  override def loadLevels(): Unit = levels foreach(level => levelsContainer.children.add(new EditorLevelNode(this, level.name)))

  override def onLevelDeleteClick(level: String): Unit = {}

}

/**
  * Trait which gets notified when a LevelSelectionScene event occurs
  */
trait EditorLevelSelectionSceneListener extends LevelSelectionSceneListener with EditorSceneListener {

  /**
    * This method retrieves the custom levels that must be shown as node
    * @return a list of custom levels
    */
  def getCustomLevels: List[LevelInfo]
}
