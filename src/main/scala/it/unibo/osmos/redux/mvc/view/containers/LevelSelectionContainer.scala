package it.unibo.osmos.redux.mvc.view.containers

import it.unibo.osmos.redux.mvc.view.components.level.{LevelNode, LevelNodeListener}
import it.unibo.osmos.redux.mvc.view.context.LevelContext
import scalafx.geometry.Pos
import scalafx.scene.layout.{TilePane, VBox}
import scalafx.stage.Stage

class LevelSelectionContainer(parentStage: Stage) extends LevelNodeListener {

  /**
    * The central level container
    */
  protected val levelsContainer: TilePane = new TilePane() {
    alignmentInParent = Pos.Center
    alignment = Pos.Center
    prefColumns = numLevels
    prefRows = 1
    prefHeight <== parentStage.height
  }

  protected val container: VBox = new VBox {
    alignment = Pos.Center
    /* Loading the levels */
    loadLevels()
    children = Seq(levelsContainer)
  }

  def getContainer: VBox = container

  //TODO: get the proper number of levels
  /**
    * The number of levels
    * @return the number of levels
    */
  def numLevels: Int = 5

  //TODO: parse actual available values
  /**
    * This method loads the level into the level container, thus letting the player choose them
    */
  def loadLevels(): Unit = ??? //for (i <- 1 to numLevels) levelsContainer.children.add(new LevelNode(LevelSelectionContainer.this, levels, i == 1))

  def onLevelPlayClick(level: String, simulation: Boolean): Unit = {
    /* Creating a listener on the run*/
    /*val upperLevelSceneListener: UpperLevelSceneListener = () => parentStage.scene = parentStage
    /* Creating a new level scene */
    val levelScene = new LevelScene(parentStage, this, upperLevelSceneListener)
    /* Creating the level context */
    val levelContext = LevelContext(simulation)
    levelContext.setListener(levelScene)
    levelScene.levelContext = levelContext
    /* Changing scene scene */
    parentStage.scene = levelScene
    /* Notify the view the new context */
    this.onLevelContextCreated(levelContext, level)*/
    ???
  }

  /*override def onLevelContextCreated(levelContext: LevelContext, level: Int): Unit = ???

  override def onStartLevel(): Unit = ???

  override def onPauseLevel(): Unit = ???

  override def onResumeLevel(): Unit = ???

  override def onStopLevel(): Unit = ???*/
}
