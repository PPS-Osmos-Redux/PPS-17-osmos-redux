package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.levels.structure.LevelInfo
import it.unibo.osmos.redux.mvc.view.components.level.{LevelNode, LevelNodeListener}
import it.unibo.osmos.redux.mvc.view.components.menu.MainMenuBarListener
import it.unibo.osmos.redux.mvc.view.context.LevelContext
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.layout.{TilePane, VBox}
import scalafx.stage.Stage

import scala.collection.mutable

/**
  * This scene lets the players choose which level they want to play
  *
  * @param parentStage the parent stage
  * @param listener    the listener
  */
class LevelSelectionScene(override val parentStage: Stage, val listener: LevelSelectionSceneListener, previousSceneListener: BackClickListener) extends DefaultBackScene(parentStage, previousSceneListener)
  with MainMenuBarListener with LevelNodeListener {

  /**
    * The levels shown
    *
    * @return the list of levels as LevelInfo
    */
  lazy val levels: mutable.Buffer[LevelInfo] = listener.getSingleLevels.toBuffer

  /**
    * This method loads the level into the level container, thus letting the player choose them
    */
  def loadLevels(): Unit = levels foreach (level => levelsContainer.children.add(new LevelNode(LevelSelectionScene.this, level, levels.indexOf(level))))

  /**
    * This method refreshes the level list to acquire the new LevelInfos
    */
  protected def refreshLevels() : Unit = {
    levels.clear()
    levels.appendAll(listener.getSingleLevels)
  }

  /**
    * This method refreshes the level list and reload the nodes
    */
  private def reloadLevels() : Unit = {
    refreshLevels()
    levelsContainer.children.clear()
    loadLevels()
  }

  /**
    * The central level container
    */
  protected val levelsContainer: TilePane = new TilePane() {
    alignmentInParent = Pos.Center
    alignment = Pos.Center
    prefColumns = levels.size
    prefRows = 1
    prefHeight <== parentStage.height
  }

  protected val buttonsContainer: VBox = new VBox(10) {
    alignment = Pos.Center
    margin = Insets(0, 0, 40, 0)
    children = goBack
  }

  protected val container: VBox = new VBox(10) {
    alignment = Pos.Center
    /* Loading the levels */
    loadLevels()
    children = Seq(levelsContainer, buttonsContainer)
  }

  /* Setting the root container*/
  root = container

  override def onFullScreenSettingClick(): Unit = {
    parentStage.fullScreen = !parentStage.fullScreen.get()
  }

  /**
    * Called when the user want to play a level
    *
    * @param levelInfo  the level info
    * @param simulation true if the level must be started as a simulation, false otherwise
    * @param custom     true if the level is a custom one, false otherwise
    */
  def onLevelPlayClick(levelInfo: LevelInfo, simulation: Boolean, custom: Boolean = false): Unit = {
    /* Creating a listener on the run*/
    val upperLevelSceneListener: UpperLevelSceneListener = () => {parentStage.scene = this; reloadLevels()}
    /* Creating a new level scene */
    val levelScene = new LevelScene(parentStage, levelInfo, listener, upperLevelSceneListener)
    /* Creating the level context */
    val levelContext = LevelContext(simulation)
    levelContext.setListener(levelScene)
    levelScene.levelContext = levelContext
    /* Changing scene scene */
    parentStage.scene = levelScene
    /* Notify the view the new context */
    listener.onLevelContextCreated(levelContext, levelInfo.name, custom)
  }

}

/**
  * Trait which gets notified when a LevelSelectionScene event occurs
  */
trait LevelSelectionSceneListener extends LevelSceneListener {

  /**
    * This method called when the level context has been created
    *
    * @param levelContext the new level context
    * @param level        the new level name
    * @param isCustom     true if the level is custom, false otherwise
    */
  def onLevelContextCreated(levelContext: LevelContext, level: String, isCustom: Boolean = false)

  /**
    * This method retrieves the levels that must be shown as node
    *
    * @return a list of LevelInfo
    */
  def getSingleLevels: List[LevelInfo]
}
