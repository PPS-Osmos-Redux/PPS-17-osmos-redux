package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.levels.structure.LevelInfo
import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import it.unibo.osmos.redux.mvc.view.components.editor.{EditorLevelNode, EditorLevelNodeListener}
import scalafx.geometry.Pos
import scalafx.stage.Stage

import scala.collection.mutable

/** Scene in which the user can see the created custom levels
  *
  * @param parentStage the parent stage
  * @param listener    the listener
  */
class EditorLevelSelectionScene(override val parentStage: Stage, override val listener: EditorLevelSelectionSceneListener, previousSceneListener: BackClickListener) extends LevelSelectionScene(parentStage, listener, previousSceneListener)
  with EditorLevelNodeListener {

  /** Custom levels are always available */
  override lazy val levels: mutable.Buffer[LevelInfo] = listener.getCustomLevels.toBuffer

  private val newLevelButton = new StyledButton("Create new level") {
    alignment = Pos.BottomCenter
    alignmentInParent = Pos.BottomCenter
    /** We open the editor. On level saved we recreate the scene */
    onAction = _ => parentStage.scene = new EditorScene(parentStage, listener, () => parentStage.scene = new EditorLevelSelectionScene(parentStage, listener, previousSceneListener))
  }
  /** Add newLevelButton before goBack button */
  buttonsContainer.children.add(buttonsContainer.children.size() - 1, newLevelButton)

  override def onLevelPlayClick(levelInfo: LevelInfo, simulation: Boolean, custom: Boolean = false): Unit = super.onLevelPlayClick(levelInfo, simulation, custom = true)

  override def loadLevels(): Unit = levels foreach (level => levelsContainer.children.add(new EditorLevelNode(this, level)))

  /** Overridden to manage custom levels */
  override def refreshLevels(): Unit = {
    levels.clear()
    levels.appendAll(listener.getCustomLevels)
  }

  override def onLevelDeleteClick(level: String): Unit = listener.onDeleteLevel(level, _ => parentStage.scene = new EditorLevelSelectionScene(parentStage, listener, previousSceneListener))

}

/** Trait which gets notified when a LevelSelectionScene event occurs */
trait EditorLevelSelectionSceneListener extends LevelSelectionSceneListener with EditorSceneListener {

  /** This method retrieves the custom levels that must be shown as node
    *
    * @return a list of custom levels
    */
  def getCustomLevels: List[LevelInfo]

  /** Called when the user wants to delete a custom level
    *
    * @param level    the level name
    * @param callback the callback
    */
  def onDeleteLevel(level: String, callback: Boolean => Unit): Unit
}
