package it.unibo.osmos.redux.mvc.view.drawables

import scalafx.scene.canvas.GraphicsContext

/**
  * Abstract base Drawable class which holds spacial coordinates and the reference of the GraphicContext on which it will be drawn
  * @param graphicsContext the GraphicContext on which the Drawable will be drawn on
  */
abstract class BaseDrawable(val graphicsContext: GraphicsContext) extends Drawable {

}
