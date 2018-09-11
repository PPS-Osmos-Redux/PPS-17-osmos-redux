package it.unibo.osmos.redux.mvc.view.drawables

import it.unibo.osmos.redux.mvc.view.ViewConstants.Window.{HalfWindowHeight, HalfWindowWidth}
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

/**
  * Drawable implementation that shows a cell on the screen
  *
  * @param image           the image
  * @param graphicsContext the GraphicContext on which the Image will be drawn on
  */
class CellDrawable(override val image: Image, override val graphicsContext: GraphicsContext) extends CircleDrawable(graphicsContext) with ImageDrawable {

  /**
    * Draws the circular image on the canvas
    *
    * @param dw    the drawable wrapper containing the drawable info
    * @param color the color of the circle, currently unused
    */
  override def draw(dw: DrawableWrapper, color: Color): Unit = {
    graphicsContext.stroke = color
    graphicsContext.lineWidth = 2
    val xPosition = dw.center.x - dw.radius + HalfWindowWidth
    val yPosition = dw.center.y - dw.radius + HalfWindowHeight
    // since it is a circle the width diameter
    // is equal to the height diameter
    val diameter = dw.radius * 2
    graphicsContext.fillOval(xPosition, yPosition, diameter, diameter)
    graphicsContext.strokeOval(xPosition, yPosition, diameter, diameter)
    graphicsContext.drawImage(image, xPosition, yPosition, diameter, diameter)
  }

}
