package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.LevelInfo
import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import it.unibo.osmos.redux.mvc.view.components.editor.{EditorLevelNode, EditorLevelNodeListener}
import scalafx.geometry.Pos
import scalafx.stage.Stage

/**
  * Scene in which the user can see the created custom levels
  *
  * @param parentStage the parent stage
  * @param listener    the listener
  */
class EditorLevelSelectionScene(override val parentStage: Stage, override val listener: EditorLevelSelectionSceneListener, previousSceneListener: BackClickListener) extends LevelSelectionScene(parentStage, listener, previousSceneListener)
  with EditorLevelNodeListener {

  private val newLevelButton = new StyledButton("Create new level") {
    alignment = Pos.BottomCenter
    alignmentInParent = Pos.BottomCenter
    /* We open the editor. On level saved we recreate the scene */
    onAction = _ => parentStage.scene = new EditorScene(parentStage, listener, () => parentStage.scene = new EditorLevelSelectionScene(parentStage, listener, previousSceneListener))
  }

  // TODO: check if can it be done better
  buttonsContainer.children.add(buttonsContainer.children.size() - 1, newLevelButton)

  override def onLevelPlayClick(levelInfo: LevelInfo, simulation: Boolean, custom: Boolean = false): Unit = super.onLevelPlayClick(levelInfo, simulation, custom = true)

  /** Custom levels are always available */
  override def levels: List[LevelInfo] = listener.getCustomLevels

  override def loadLevels(): Unit = levels foreach (level => levelsContainer.children.add(new EditorLevelNode(this, level)))

  override def onLevelDeleteClick(level: String): Unit = listener.onDeleteLevel(level, _ => parentStage.scene = new EditorLevelSelectionScene(parentStage, listener, previousSceneListener))

}

/**
  * Trait which gets notified when a LevelSelectionScene event occurs
  */
trait EditorLevelSelectionSceneListener extends LevelSelectionSceneListener with EditorSceneListener {

  /**
    * This method retrieves the custom levels that must be shown as node
    *
    * @return a list of custom levels
    */
  def getCustomLevels: List[LevelInfo]

  /**
    * Called when the user wants to delete a custom level
    *
    * @param level    the level name
    * @param callback the callback
    */
  def onDeleteLevel(level: String, callback: Boolean => Unit): Unit
}
