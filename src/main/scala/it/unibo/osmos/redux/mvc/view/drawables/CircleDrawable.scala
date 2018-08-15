package it.unibo.osmos.redux.mvc.view.drawables

import it.unibo.osmos.redux.utils.Point
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
/**
  * Drawable implementation that shows a circle on the screen
  * @param graphicsContext the GraphicContext on which the circle will be drawn on
  */
class CircleDrawable(override val graphicsContext: GraphicsContext) extends BaseDrawable(graphicsContext) {

  /**
    * Draws a circle on the canvas
    * @param point the center of the circle
    * @param radius the radius of the circle
    * @param color the color of the circle
    */
  def draw(point: Point, radius: Double, color: Color): Unit = {
    graphicsContext.fill = color
    graphicsContext.fillOval(point.x - radius, point.y - radius, radius * 2, radius * 2)
    //graphicsContext.strokeOval(point.x - radius, point.y - radius, radius * 2, radius * 2)
  }
}
