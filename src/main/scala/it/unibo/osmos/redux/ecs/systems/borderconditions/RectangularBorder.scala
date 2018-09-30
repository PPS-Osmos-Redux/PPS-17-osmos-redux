package it.unibo.osmos.redux.ecs.systems.borderconditions

import it.unibo.osmos.redux.ecs.entities.properties.composed.CollidableProperty
import it.unibo.osmos.redux.mvc.controller.levels.structure.CollisionRules
import it.unibo.osmos.redux.utils.{MathUtils, Point, Vector}

/** Collision implementation for a playing field with rectangular shape */
case class RectangularBorder(levelCenter: Point, collisionRule: CollisionRules.Value, base: Double, height: Double) extends AbstractBorder(levelCenter, collisionRule) {

  private var currentPositionCopy: Point = _
  /** minimum horizontal position reachable by the current entity */
  private var minHorizontalPoint: Double = _
  /** maximum horizontal position reachable by the current entity */
  private var maxHorizontalPoint: Double = _
  /** minimum vertical position reachable by the current entity */
  private var minVerticalPoint: Double = _
  /** maximum vertical position reachable by the current entity */
  private var maxVerticalPoint: Double = _

  override protected def initCollisionParameters(entity: CollidableProperty): Unit = {
    super.initCollisionParameters(entity)
    currentPositionCopy = currentPosition
    minHorizontalPoint = getLowerBoundary(entityRadius, levelCenter.x, base)
    maxHorizontalPoint = getUpperBoundary(entityRadius, levelCenter.x, base)
    minVerticalPoint = getLowerBoundary(entityRadius, levelCenter.y, height)
    maxVerticalPoint = getUpperBoundary(entityRadius, levelCenter.y, height)
  }

  private def getLowerBoundary(radius: Double, centerCoordinate: Double, borderLength: Double): Double = {
    centerCoordinate - borderLength / 2 + radius
  }

  private def getUpperBoundary(radius: Double, centerCoordinate: Double, borderLength: Double): Double = {
    centerCoordinate + borderLength / 2 - radius
  }

  override protected def hasCollidedWithBorder: Boolean = {
    currentPosition.x < minHorizontalPoint || currentPosition.x > maxHorizontalPoint || currentPosition.y < minVerticalPoint || currentPosition.y > maxVerticalPoint
  }

  override protected def computeNewPosition(): Point = {
    val newX = MathUtils.clamp(currentPosition.x, minHorizontalPoint, maxHorizontalPoint)
    val newY = MathUtils.clamp(currentPosition.y, minVerticalPoint, maxVerticalPoint)
    Point(newX, newY)
  }

  override protected def computeNewSpeed(newPosition: Point): Vector = {
    val newSpeedX = if (newPosition.x == currentPositionCopy.x) speedComponent.vector.x else -speedComponent.vector.x
    val newSpeedY = if (newPosition.y == currentPositionCopy.y) speedComponent.vector.y else -speedComponent.vector.y
    Vector(newSpeedX, newSpeedY)
  }

  override protected def computeNewRadius(): Double = {
    val resX = getPortionOutsideBorder(currentPosition.x, minHorizontalPoint, maxHorizontalPoint)
    val resY = getPortionOutsideBorder(currentPosition.y, minVerticalPoint, maxVerticalPoint)
    entityRadius - resX - resY
  }

  private def getPortionOutsideBorder(position: Double, minReachablePosition: Double, maxReachablePosition: Double): Double = {
    position match {
      case p if p < minReachablePosition => minReachablePosition - p
      case p if p > maxReachablePosition => p - maxReachablePosition
      case _ => 0 // the entity is inside the map edge => radius portion outside the map is 0
    }
  }

  override protected def reposition(): Unit = positionComponent.point_(computeNewPosition())
}
