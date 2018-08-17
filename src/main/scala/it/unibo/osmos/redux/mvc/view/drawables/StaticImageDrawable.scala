package it.unibo.osmos.redux.mvc.view.drawables
import it.unibo.osmos.redux.utils.Point
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image

/**
  * Represent a static image
  * @param image the image
  * @param graphicsContext the graphic context
  */
class StaticImageDrawable(override val image: Image, val center: Point, val width: Double, val height:Double, override val graphicsContext: GraphicsContext) extends ImageDrawable {

  /**
    * Method which draws the image on the screen
    */
  def draw(): Unit = graphicsContext.drawImage(image, center.x - width, center.y - height, width * 2, height * 2)

}
