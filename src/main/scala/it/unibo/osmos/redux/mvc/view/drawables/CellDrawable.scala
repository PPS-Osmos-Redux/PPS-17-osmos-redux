package it.unibo.osmos.redux.mvc.view.drawables
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

/**
  * Drawable implementation that shows a cell on the screen
  * @param image the image
  * @param graphicsContext the GraphicContext on which the Image will be drawn on
  */
class CellDrawable(override val image: Image, override val graphicsContext: GraphicsContext) extends CircleDrawable(graphicsContext) with ImageDrawable {

  /**
    * Draws the circular image on the canvas
    * @param dw the drawable wrapper containing the drawable info
    * @param color the color of the circle, currently unused
    */
  override def draw(dw: DrawableWrapper, color: Color): Unit = {
    graphicsContext.stroke = color
    graphicsContext.lineWidth = 2
    graphicsContext.fillOval(dw.center.x - dw.radius, dw.center.y - dw.radius, dw.radius * 2, dw.radius * 2)
    graphicsContext.strokeOval(dw.center.x - dw.radius, dw.center.y - dw.radius, dw.radius * 2, dw.radius * 2)
    graphicsContext.drawImage(image, dw.center.x - dw.radius, dw.center.y - dw.radius, dw.radius * 2, dw.radius * 2)
  }

}
