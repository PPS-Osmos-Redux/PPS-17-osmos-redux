package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.levels.LevelContext
import it.unibo.osmos.redux.mvc.view.components.{LevelNode, LevelNodeListener, MainMenuBar, MainMenuBarListener}
import scalafx.geometry.Pos
import scalafx.scene.layout.{TilePane, VBox}
import scalafx.stage.Stage

/**
  * This scene lets the players choose which level they want to play
  */
class LevelSelectionScene(override val parentStage: Stage, val listener: LevelSelectionSceneListener) extends BaseScene(parentStage)
  with MainMenuBarListener with LevelNodeListener with LevelSceneListener {

  val numLevels = 5

  root = new VBox {
    children = Seq(new MainMenuBar(LevelSelectionScene.this),
      new TilePane() {
        alignmentInParent = Pos.Center
        alignment = Pos.Center
        prefColumns = numLevels
        prefRows = 1
        minHeight <== parentStage.height
        //TODO: parse actual available values
        for (i <- 1 to numLevels) children.add(new LevelNode(LevelSelectionScene.this, i, if (i == 1) true else false))
      }
    )
  }

  override def onFullScreenSettingClick(): Unit = {
    parentStage.fullScreen = !parentStage.fullScreen.get()
  }

  def onLevelPlayClick(level: Int, simulation: Boolean): Unit = {
    // Creating a new level scene
    val levelScene = new LevelScene(parentStage, this)
    // Creating a new LevelContext and setting it to the scene
    val levelContext = LevelContext(levelScene, simulation)
    levelScene.levelContext = levelContext
    // Changing scene scene
    parentStage.scene = levelScene
    // Notify the view the new context
    listener.onLevelContextSetup(levelContext)
  }

}

/**
  * Trait which gets notified when a LevelSelectionSceneListener event occurs
  */
trait LevelSelectionSceneListener {

  /**
    * This method called when the level context has been created
    * @param levelContext the new level context
    */
  def onLevelContextSetup(levelContext: LevelContext)
}
