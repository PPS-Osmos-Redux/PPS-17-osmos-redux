package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.components.level.{LevelNode, LevelNodeListener}
import it.unibo.osmos.redux.mvc.view.components.menu.{MainMenuBar, MainMenuBarListener}
import it.unibo.osmos.redux.mvc.view.context.LevelContext
import scalafx.geometry.Pos
import scalafx.scene.layout.{TilePane, VBox}
import scalafx.stage.Stage

/**
  * This scene lets the players choose which level they want to play
  */
class LevelSelectionScene(override val parentStage: Stage, val listener: LevelSelectionSceneListener) extends BaseScene(parentStage)
  with MainMenuBarListener with LevelNodeListener {

  /**
    * The upper main menu bar
    */
  protected val menuBar = new MainMenuBar(this)

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

  protected val container: VBox = new VBox {
    alignment = Pos.Center
    /* Loading the levels */
    loadLevels()
    children = Seq(menuBar, levelsContainer)
  }

  /* Setting the root container*/
  root = container

  /**
    * The levels shown
    * @return the list of levels as tuples where the first element is the name and the second a boolean (true if the level is available, false otherwise)
    */
  def levels: List[(String, Boolean)] = listener.getSingleLevels

  /**
    * This method loads the level into the level container, thus letting the player choose them
    */
  //TODO: FIX HERE STRING OR INT?
  def loadLevels(): Unit = levels foreach((level) => levelsContainer.children.add(new LevelNode(LevelSelectionScene.this, 1 /*level._1*/, level._2)))

  override def onFullScreenSettingClick(): Unit = {
    parentStage.fullScreen = !parentStage.fullScreen.get()
  }

  def onLevelPlayClick(level: Int, simulation: Boolean): Unit = {
    /* Creating a listener on the run*/
    val upperLevelSceneListener: UpperLevelSceneListener = () => parentStage.scene = this
    /* Creating a new level scene */
    val levelScene = new LevelScene(parentStage, listener, upperLevelSceneListener)
    /* Creating the level context */
    val levelContext = LevelContext(simulation)
    levelContext.setListener(levelScene)
    levelScene.levelContext = levelContext
    /* Changing scene scene */
    parentStage.scene = levelScene
    /* Notify the view the new context */
    listener.onLevelContextCreated(levelContext, level)
  }

}

/**
  * Trait which gets notified when a LevelSelectionScene event occurs
  */
trait LevelSelectionSceneListener extends LevelSceneListener {

  /**
    * This method called when the level context has been created
    * @param levelContext the new level context
    * @param level the new level index
    */
  def onLevelContextCreated(levelContext: LevelContext, level: Int)

  /**
    * This method retrieves the levels that must be shown as node
    * @return a list of tuples where a single tuple represent a level name and a boolean (true if available, false otherwise)
    */
  def getSingleLevels: List[(String, Boolean)]
}
