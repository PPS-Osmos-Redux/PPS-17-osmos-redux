package it.unibo.osmos.redux.mvc.view.drawables

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

/** Drawable implementation that shows a circle on the screen
  *
  * @param graphicsContext the GraphicContext on which the circle will be drawn on
  */
class CircleDrawable(val graphicsContext: GraphicsContext) extends Drawable {

  /** Draws a circle on the canvas
    *
    * @param dw    the drawable wrapper containing the drawable info
    * @param color the color
    */
  def draw(dw: DrawableWrapper, color: Color): Unit = {
    graphicsContext.fill = color
    graphicsContext.fillOval(dw.center.x - dw.radius, dw.center.y - dw.radius, dw.radius * 2, dw.radius * 2)
  }
}
