package it.unibo.osmos.redux.utils

/**
  * Utility class that offers math related useful methods
  */
object MathUtils {

  /**
    * Returns the normalized value of a number between a min and a max
    *
    * @param number the number
    * @param min    the min number
    * @param max    the max number
    * @return the normalized number between min and max
    */
  def normalize(number: Double, min: Double, max: Double): Double = (number - min) / (max - min)

  /**
    * Returns the normalized point.
    *
    * @param point The point to normalize
    * @return The normalized point
    */
  def normalizePoint(point: Point): Point = {
    val mod = math.sqrt(math.pow(point.x, 2) + math.pow(point.y, 2))
    Point(point.x / mod, point.y / mod)
  }

  /**
    * Returns the distance between two points.
    *
    * @param p1 The first point
    * @param p2 The second point
    * @return The distance
    */
  def distanceBetweenPoints(p1: Point, p2: Point): Double = {
    math.sqrt(math.pow(p2.x - p1.x, 2) + math.pow(p2.y - p1.y, 2))
  }

  def isPointBetweenPoints(p: Point, p1: Point, p2: Point): Boolean = {
    val distance = MathUtils.distanceBetweenPoints(p1, p2)
    MathUtils.distanceBetweenPoints(p, p1) < distance && MathUtils.distanceBetweenPoints(p, p2) < distance
  }

}
