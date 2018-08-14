package it.unibo.osmos.redux.utils

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

  /**
    * Returns the normalized point
    * @param point The point to normalize
    * @return The normalized point
    */
  def normalizePoint(point: Point): Point = {
    val mod = math.sqrt(math.pow(point.x, 2) + math.pow(point.y, 2))
    Point(point.x / mod, point.y / mod)
  }

  def euclideanDistance(point1: Point, point2: Point): Double = Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2))
}
