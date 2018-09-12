package it.unibo.osmos.redux.utils

import it.unibo.osmos.redux.ecs.components.PositionComponent

/** Utility class that offers math related useful methods */
object MathUtils {

  /** Returns the normalized value of a number between a min and a max
    *
    * @param number the number
    * @param min    the min number
    * @param max    the max number
    * @return the normalized number between min and max
    */
  def normalize(number: Double, min: Double, max: Double): Double = (number - min) / (max - min)

  /** unitVector from point2 to point1
    *
    * @param point1
    * @param point2
    * @return unitVector
    */
  def unitVector(point1: Point, point2: Point): Vector = {
    point1 subtract point2 normalized()
  }

  /** Returns the square of Euclidean distance in 2D space
    *
    * @param point1 first point
    * @param point2 second point
    * @return square of Euclidean distance
    */
  def euclideanDistanceSq(point1: Point, point2: Point): Double =
    Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2)

  /** Returns the Euclidean distance in 2D space
    *
    * @param position1 first position
    * @param position2 second position
    * @return Euclidean distance
    */
  def euclideanDistance(position1: PositionComponent, position2: PositionComponent): Double =
    euclideanDistance(position1.point, position2.point)

  /** Returns the Euclidean distance in 2D space
    *
    * @param point1 first point
    * @param point2 second point
    * @return Euclidean distance
    */
  def euclideanDistance(point1: Point, point2: Point): Double =
    Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2))

  /** Returns the area of a circle of a given radius
    *
    * @param radius the radius
    * @return area
    */
  def circleArea(radius: Double): Double = Math.pow(radius, 2) * Math.PI

  /** Return the radius of a circle of a given area
    *
    * @param area the area
    * @return the radius
    */
  def areaToRadius(area: Double): Double = Math.sqrt(area / Math.PI)

  /** Method which limits a value between a minimum and a maximum ones
    *
    * @param value the value
    * @param min   the minimum value
    * @param max   the maximum value
    * @return the clamped value
    */
  def clamp(value: Double, min: Double, max: Double): Double = {
    value match {
      case v if v < min => min
      case v if v > max => max
      case _ => value
    }
  }
}
