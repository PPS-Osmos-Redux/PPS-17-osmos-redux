package it.unibo.osmos.redux.main.utils

/**
  * Utility class that offers math related useful methods
  */
object MathUtils {

  /**
    * Returns the normalized value of a number between a min and a max
    * @param number the number
    * @param min the min number
    * @param max the max number
    * @return the normalized number between min and max
    */
  def normalize(number: Double, min: Double, max: Double): Double = (number - min)/(max - min)

}
