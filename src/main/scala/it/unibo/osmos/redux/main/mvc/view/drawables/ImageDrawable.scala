package it.unibo.osmos.redux.main.mvc.view.drawables
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

  override def draw(x: Double, y: Double, width: Double, height: Double): Unit = {
    graphicsContext.drawImage(_image, x, y, width, height)
  }
}
