package it.unibo.osmos.redux.ecs.systems.borderconditions

import it.unibo.osmos.redux.ecs.components.{DimensionComponent, PositionComponent, SpeedComponent}
import it.unibo.osmos.redux.ecs.entities.properties.composed.CollidableProperty
import it.unibo.osmos.redux.mvc.controller.levels.structure.CollisionRules
import it.unibo.osmos.redux.utils.{MathUtils, Point, Vector}

/** Collision implementation for a playing field with rectangular shape */
case class RectangularBorder(levelCenter: Point, collisionRule: CollisionRules.Value, base: Double, height: Double) extends AbstractBorder(levelCenter, collisionRule) {

  private var dimensionComponent: DimensionComponent = _
  private var entityRadius: Double = _
  private var speedComponent: SpeedComponent = _
  /** minimum horizontal position reachable by the current entity */
  private var minHorizontalPoint: Double = _
  /** maximum horizontal position reachable by the current entity */
  private var maxHorizontalPoint: Double = _
  /** minimum vertical position reachable by the current entity */
  private var minVerticalPoint: Double = _
  /** maximum vertical position reachable by the current entity */
  private var maxVerticalPoint: Double = _
  private var positionComponent: PositionComponent = _
  private var position: Point = _

  override def checkAndSolveCollision(entity: CollidableProperty): Unit = {
    initEntityCollisionData(entity)
    collisionRule match {
      case CollisionRules.bouncing =>
        val newPosition = computeNewPosition()
        val newSpeed = computeNewSpeed(newPosition)
        positionComponent.point_(newPosition)
        speedComponent.vector_(newSpeed)
      case CollisionRules.instantDeath =>
        computeNewRadius(position.x, minHorizontalPoint, maxHorizontalPoint)
        computeNewRadius(position.y, minVerticalPoint, maxVerticalPoint)
      case _ => throw new IllegalArgumentException
    }
  }

  private def initEntityCollisionData(entity: CollidableProperty): Unit = {
    dimensionComponent = entity.getDimensionComponent
    entityRadius = dimensionComponent.radius
    speedComponent = entity.getSpeedComponent
    minHorizontalPoint = getLowerBoundary(entityRadius, levelCenter.x, base)
    maxHorizontalPoint = getUpperBoundary(entityRadius, levelCenter.x, base)
    minVerticalPoint = getLowerBoundary(entityRadius, levelCenter.y, height)
    maxVerticalPoint = getUpperBoundary(entityRadius, levelCenter.y, height)
    positionComponent = entity.getPositionComponent
    position = positionComponent.point
  }

  private def getLowerBoundary(radius: Double, centerCoordinate: Double, borderLength: Double): Double = {
    centerCoordinate - borderLength / 2 + radius
  }

  private def getUpperBoundary(radius: Double, centerCoordinate: Double, borderLength: Double): Double = {
    centerCoordinate + borderLength / 2 - radius
  }

  private def computeNewPosition(): Point = {
    val newX = MathUtils.clamp(position.x, minHorizontalPoint, maxHorizontalPoint)
    val newY = MathUtils.clamp(position.y, minVerticalPoint, maxVerticalPoint)
    Point(newX, newY)
  }

  private def computeNewSpeed(point: Point): Vector = {
    val newSpeedX: Double = if (point.x == position.x) speedComponent.vector.x else -speedComponent.vector.x
    val newSpeedY: Double = if (point.y == position.y) speedComponent.vector.y else -speedComponent.vector.y
    Vector(newSpeedX, newSpeedY)
  }

  private def computeNewRadius(position: Double, minReachablePosition: Double, maxReachablePosition: Double): Unit = {
    val entityRadius = dimensionComponent.radius
    position match {
      case p if p < minReachablePosition => dimensionComponent.radius_(entityRadius - (minReachablePosition - p))
      case p if p > maxReachablePosition => dimensionComponent.radius_(entityRadius - (p - maxReachablePosition))
      case _ => // no border collision, do nothing
    }
  }

  override def repositionIfOutsideMap(entity: CollidableProperty): Unit = {
    initEntityCollisionData(entity)
    positionComponent.point_(computeNewPosition())
  }
}
