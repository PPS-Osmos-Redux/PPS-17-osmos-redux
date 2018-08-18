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
    * Returns the Euclidean distance in 2D space
    * @param point1 first point
    * @param point2 second point
    * @return Euclidean distance
    */
  def euclideanDistance(point1: Point, point2: Point): Double =
    Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2))

  /**
    * Returns the area of a circle of a given radius
    * @param radius the radius
    * @return area
    */
  def circleArea(radius: Double): Double = Math.pow(radius, 2) * Math.PI

  def isPointBetweenPoints(p: Point, p1: Point, p2: Point): Boolean = {
    val distance = MathUtils.euclideanDistance(p1, p2)
    MathUtils.euclideanDistance(p, p1) < distance && MathUtils.euclideanDistance(p, p2) < distance
  }
}
