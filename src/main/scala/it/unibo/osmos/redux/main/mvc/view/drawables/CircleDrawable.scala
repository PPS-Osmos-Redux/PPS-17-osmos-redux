package it.unibo.osmos.redux.main.mvc.view.drawables

import scalafx.scene.canvas.GraphicsContext
/**
  * Drawable implementation that shows a circle on the screen
  * @param graphicsContext the GraphicContext on which the circle will be drawn on
  */
class CircleDrawable(override val graphicsContext: GraphicsContext) extends BaseDrawable(graphicsContext) {

  override def draw(x: Double, y: Double, width: Double, height: Double): Unit = {
    graphicsContext.fillOval(x, y, width, height)
  }
}
