package it.unibo.osmos.redux.main.mvc.view.drawables
import it.unibo.osmos.redux.main.utils.Point
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image

/**
  * Drawable implementation that shows an Image on the screen
  * @param _image the image
  * @param graphicsContext the GraphicContext on which the Image will be drawn on
  */
class ImageDrawable(private var _image: Image, override val graphicsContext: GraphicsContext) extends BaseDrawable(graphicsContext) {

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

  def draw(point: Point, width: Double, height: Double): Unit = {
    graphicsContext.drawImage(_image, point.x, point.y, width, height)
  }
}
