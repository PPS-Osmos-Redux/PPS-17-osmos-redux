package it.unibo.osmos.redux.mvc.view.drawables
import it.unibo.osmos.redux.utils.Point
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
    * @param point the center of the image
    * @param radius the radius of the circle
    * @param color the color of the circle
    */
  override def draw(point: Point, radius: Double, color: Color): Unit = {
    graphicsContext.fill = color
    graphicsContext.drawImage(image, point.x - radius, point.y - radius, radius * 2, radius * 2)
    /*graphicsContext.fill = color
    graphicsContext.fillOval(point.x - radius, point.y - radius, radius * 2, radius * 2)
    graphicsContext.strokeOval(point.x - radius, point.y - radius, radius * 2, radius * 2)*/
  }
}
