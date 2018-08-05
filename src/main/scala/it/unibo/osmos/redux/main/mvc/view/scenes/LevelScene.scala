package it.unibo.osmos.redux.main.mvc.view.scenes

import it.unibo.osmos.redux.main.mvc.view.drawables.CircleDrawable
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
  val circleDrawable: CircleDrawable = new CircleDrawable(canvas.graphicsContext2D)

  onMouseClicked = mouseEvent => levelContext.pushMouseEvent(mouseEvent)

  override def onDrawEntities(drawables: Seq[(Double, Double, Double, Double)]): Unit = {
    /* We must draw to the screen the entire collection */
    Platform.runLater({
      canvas.graphicsContext2D.clearRect(parentStage.getX, parentStage.getY, parentStage.getWidth, parentStage.getHeight)
      drawables foreach(e => circleDrawable.draw(e._1, e._2, e._3, e._4))
    })
  }
}
