package it.unibo.osmos.redux.ecs.systems.borderconditions

import it.unibo.osmos.redux.ecs.components.DimensionComponent
import it.unibo.osmos.redux.ecs.entities.properties.composed.CollidableProperty
import it.unibo.osmos.redux.mvc.controller.levels.structure.CollisionRules
import it.unibo.osmos.redux.utils.{Point, Vector}

/** Collision implementation for a playing field with rectangular shape */
case class RectangularBorder(levelCenter: Point, collisionRule: CollisionRules.Value, base: Double, height: Double) extends AbstractBorder(levelCenter, collisionRule) {

  override def checkAndSolveCollision(entity: CollidableProperty): Unit = {
    val dimensionComponent = entity.getDimensionComponent
    val entityRadius = dimensionComponent.radius
    val speedComponent = entity.getSpeedComponent
    val minHorizontalPoint = getLowerBoundary(entityRadius, levelCenter.x, base)
    val maxHorizontalPoint = getUpperBoundary(entityRadius, levelCenter.x, base)
    val minVerticalPoint = getLowerBoundary(entityRadius, levelCenter.y, height)
    val maxVerticalPoint = getUpperBoundary(entityRadius, levelCenter.y, height)
    val positionComponent = entity.getPositionComponent
    val position = positionComponent.point

    collisionRule match {
      case CollisionRules.bouncing =>
        val speedVector = speedComponent.vector
        val rx = computeNewSpeedAndPosition(speedVector.x, position.x, minHorizontalPoint, maxHorizontalPoint)
        val ry = computeNewSpeedAndPosition(speedVector.y, position.y, minVerticalPoint, maxVerticalPoint)
        speedComponent.vector_(Vector(rx._1, ry._1))
        positionComponent.point_(Point(rx._2, ry._2))
      case CollisionRules.instantDeath =>
        computeNewRadius(dimensionComponent, position.x, minHorizontalPoint, maxHorizontalPoint)
        computeNewRadius(dimensionComponent, position.y, minVerticalPoint, maxVerticalPoint)
      case _ => throw new IllegalArgumentException
    }
  }

  private def getLowerBoundary(radius: Double, centerCoordinate: Double, borderLength: Double): Double = {
    centerCoordinate - borderLength / 2 + radius
  }

  private def getUpperBoundary(radius: Double, centerCoordinate: Double, borderLength: Double): Double = {
    centerCoordinate + borderLength / 2 - radius
  }

  private def computeNewSpeedAndPosition(speed: Double, position: Double, minReachablePosition: Double, maxReachablePosition: Double): (Double, Double) = {
    position match {
      case p if p < minReachablePosition => (-speed, minReachablePosition - (p - minReachablePosition))
      case p if p > maxReachablePosition => (-speed, maxReachablePosition - (p - maxReachablePosition))
      case _ => (speed, position)
    }
  }

  private def computeNewRadius(dimensionComponent: DimensionComponent, position: Double, minReachablePosition: Double, maxReachablePosition: Double): Unit = {
    val entityRadius = dimensionComponent.radius
    position match {
      case p if p < minReachablePosition => dimensionComponent.radius_(entityRadius - (minReachablePosition - p))
      case p if p > maxReachablePosition => dimensionComponent.radius_(entityRadius - (p - maxReachablePosition))
      case _ => // no border collision, do nothing
    }
  }

  override def repositionIfOutsideMap(entity: CollidableProperty): Unit = {}

}
