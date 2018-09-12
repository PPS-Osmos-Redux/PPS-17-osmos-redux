package it.unibo.osmos.redux.mvc.view.drawables

import scalafx.scene.image.Image

/** This trait represent a Drawable which holds an image */
trait ImageDrawable extends Drawable {

  /** The image stored in the Drawable
    *
    * @return the image
    */
  def image: Image

}
