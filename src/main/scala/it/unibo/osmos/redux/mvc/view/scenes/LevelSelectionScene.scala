package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.menus.{LevelNode, MainMenuBar, MainMenuBarListener}
import scalafx.geometry.Pos
import scalafx.scene.layout.{TilePane, VBox}
import scalafx.stage.Stage

/**
  * This scene lets the players choose which level they want to play
  */
class LevelSelectionScene(override val parentStage: Stage) extends BaseScene(parentStage)
  with MainMenuBarListener {

  val numLevels = 5

  root = new VBox {
    children = Seq(new MainMenuBar(LevelSelectionScene.this),
      new TilePane() {
          alignmentInParent = Pos.Center
          alignment = Pos.Center
          prefColumns = numLevels
          prefRows = 1
          minHeight <== parentStage.height
          for (i <- 1 to numLevels) children.add(new LevelNode(200, 200, i, true))
        }
    )
  }

  override def onFullScreenSettingClick(): Unit = {
    parentStage.fullScreen = !parentStage.fullScreen.get()
  }
}
