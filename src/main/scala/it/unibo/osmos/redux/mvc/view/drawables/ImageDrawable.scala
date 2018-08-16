package it.unibo.osmos.redux.mvc.view.drawables
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

/**
  * Drawable implementation that shows an Image on the screen
  * @param _image the image
  * @param graphicsContext the GraphicContext on which the Image will be drawn on
  */
class ImageDrawable(private var _image: Image, override val graphicsContext: GraphicsContext) extends CircleDrawable(graphicsContext) {

  /**
    * Getter. Return the image stored in the Drawable
    * @return the image
    */
  def image: Image = _image

  /**
    * Setter. Sets a new image to be stored in the Drawable
    * @param image the new image
    */
  def image_= (image: Image): Unit = _image = image

  /**
    * Draws the circular image on the canvas
    * @param dw the drawable wrapper containing the drawable info
    * @param color the color of the circle
    */
  override def draw(dw: DrawableWrapper, color: Color): Unit = {
    graphicsContext.fill = color
    /* Line */
    graphicsContext.stroke = color
    //graphicsContext.strokeArc(dw.center.x, dw.center.y,20, 20, -45, 240, ArcType.Open)
    graphicsContext.lineWidth = 2
    //TODO: find a better way
    graphicsContext.strokeLine(dw.center.x, dw.center.y, dw.center.x + dw.speed._1 * dw.radius, dw.center.y + dw.speed._2 * dw.radius)

    graphicsContext.stroke = Color.Black
    graphicsContext.lineWidth = 1
    graphicsContext.fillOval(dw.center.x - dw.radius, dw.center.y - dw.radius, dw.radius * 2, dw.radius * 2)
    graphicsContext.strokeOval(dw.center.x - dw.radius, dw.center.y - dw.radius, dw.radius * 2, dw.radius * 2)
    graphicsContext.drawImage(image, dw.center.x - dw.radius, dw.center.y - dw.radius, dw.radius * 2, dw.radius * 2)
  }
}
