package it.unibo.osmos.redux.utils

import it.unibo.osmos.redux.ecs.components.PositionComponent

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
  // TODO: code duplication, this is the same as the vector one
  def normalizePoint(point: Point): Point = {
    val mod = math.sqrt(math.pow(point.x, 2) + math.pow(point.y, 2))
    Point(point.x / mod, point.y / mod)
  }

  /**
    * unitVector from point2 to point1
    * @param point1
    * @param point2
    * @return unitVector
    */
  def unitVector(point1: Point, point2: Point): Vector = {
    val unitVector = point1.subtract(point2)
    val mod = math.sqrt(math.pow(unitVector.x, 2) + math.pow(unitVector.y, 2))
    //unitVector.x_(unitVector.x / mod)
    //unitVector.y_(unitVector.y / mod)
    unitVector.divide(mod)
  }

  /**
    * Returns the Euclidean distance in 2D space
    *
    * @param point1 first point
    * @param point2 second point
    * @return Euclidean distance
    */
  def euclideanDistance(point1: Point, point2: Point): Double =
    Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2))

  /**
    * Returns the Euclidean distance in 2D space
    *
    * @param position1 first position
    * @param position2 second position
    * @return Euclidean distance
    */
  def euclideanDistance(position1: PositionComponent, position2: PositionComponent): Double =
    euclideanDistance(position1.point, position2.point)

  /**
    * Returns the area of a circle of a given radius
    *
    * @param radius the radius
    * @return area
    */
  def circleArea(radius: Double): Double = Math.pow(radius, 2) * Math.PI

  /**
    * Checks if a point is between other two points and it is
    * on the straight line passing through these two points
    *
    * @param p the point to be verified
    * @param p1 one of the boundary points
    * @param p2 the other one
    * @return result of the evaluation
    */
  def isPointBetweenPoints(p: Point, p1: Point, p2: Point): Boolean = {
    val distance = MathUtils.euclideanDistance(p1, p2)
    MathUtils.euclideanDistance(p, p1) < distance && MathUtils.euclideanDistance(p, p2) < distance
  }
}
