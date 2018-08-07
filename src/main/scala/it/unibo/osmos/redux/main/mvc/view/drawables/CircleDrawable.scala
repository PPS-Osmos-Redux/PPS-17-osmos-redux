package it.unibo.osmos.redux.main.mvc.view.drawables

import it.unibo.osmos.redux.main.utils.Point
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
/**
  * Drawable implementation that shows a circle on the screen
  * @param graphicsContext the GraphicContext on which the circle will be drawn on
  */
class CircleDrawable(override val graphicsContext: GraphicsContext) extends BaseDrawable(graphicsContext) {

  def draw(point: Point, width: Double, height: Double, color: Color): Unit = {
    graphicsContext.fill = color
    graphicsContext.fillOval(point.x, point.y, width, height)
  }
}
