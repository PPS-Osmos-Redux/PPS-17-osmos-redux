package it.unibo.osmos.redux.main.mvc.view.drawables

/**
  * Base trait which represent anything that can be drawn on the screen
  */
trait Drawable {

  /**
    * This method draws the Drawable on the screen
    *
    * @param x the x-coordinate of the upper left corner
    * @param y the y-coordinate of the upper left corner
    * @param width the Drawable width
    * @param height the Drawable height
    */
  def draw(x: Double, y: Double, width: Double, height: Double)

}
