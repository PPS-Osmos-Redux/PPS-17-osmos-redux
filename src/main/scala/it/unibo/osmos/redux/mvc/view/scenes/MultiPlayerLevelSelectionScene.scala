package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.levels.structure.LevelInfo
import it.unibo.osmos.redux.mvc.view.components.level.LevelNodeListener
import it.unibo.osmos.redux.mvc.view.components.multiplayer.{MultiPlayerLevelNode, User}
import scalafx.stage.Stage

import scala.collection.mutable

/** Scene in which the user can choose a level to play in multiplayer mode
  *
  * @param parentStage the parent stage
  * @param listener the listener
  * @param upperListener the upper scene listener, which will be called to store the level selection
  * @param user the User
  */
class MultiPlayerLevelSelectionScene(override val parentStage: Stage, override val listener: MultiPlayerLevelSelectionSceneListener, val upperListener: UpperMultiPlayerLevelSelectionSceneListener, val user: User, previousSceneListener: BackClickListener)
  extends LevelSelectionScene(parentStage, listener, previousSceneListener) with LevelNodeListener {

  /** Multiplayer levels are always available */
  override lazy val levels: mutable.Buffer[LevelInfo] = listener.getMultiPlayerLevels.toBuffer

  override def loadLevels(): Unit = levels foreach(level => levelsContainer.children.add(new MultiPlayerLevelNode(this, level)))

  /** Overridden to manage multiplayer levels*/
  override def refreshLevels(): Unit = {
    levels.clear()
    levels.appendAll(listener.getMultiPlayerLevels)
  }

  override def onLevelPlayClick(levelInfo: LevelInfo, simulation: Boolean, custom: Boolean): Unit = upperListener.onLevelSelected(levelInfo)
}

trait UpperMultiPlayerLevelSelectionSceneListener {

  /** Called once when the user has selected a level
    *
    * @param levelInfo the level info
    */
  def onLevelSelected(levelInfo: LevelInfo): Unit

}

/**
  * Trait which gets notified when a MultiPlayerLevelSelectionScene event occurs
  */
trait MultiPlayerLevelSelectionSceneListener extends LevelSelectionSceneListener {

  /** This method retrieves the multiplayer levels that must be shown as node
    *
    * @return a list of multiplayer levels
    */
  def getMultiPlayerLevels: List[LevelInfo]

}
