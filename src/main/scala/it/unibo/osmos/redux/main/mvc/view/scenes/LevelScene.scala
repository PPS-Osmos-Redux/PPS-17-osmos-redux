package it.unibo.osmos.redux.main.mvc.view.scenes

import it.unibo.osmos.redux.main.mvc.view.levels.{LevelContext, LevelContextListener}
import scalafx.application.Platform
import scalafx.scene.canvas.Canvas
import scalafx.stage.Stage

/**
  * This scene holds and manages a single level
  */
class LevelScene(override val parentStage: Stage) extends BaseScene(parentStage)
  with LevelContextListener{

  val canvas: Canvas = new Canvas
  val levelContext: LevelContext = LevelContext(this)

  onMouseClicked = mouseEvent => levelContext.pushMouseEvent(mouseEvent)

  override def onDrawEntities(drawables: Seq[(Double, Double, Double, Double)]): Unit = {
    /* We must draw to the screen the entire collection */
    Platform.runLater({

    })
  }
}
