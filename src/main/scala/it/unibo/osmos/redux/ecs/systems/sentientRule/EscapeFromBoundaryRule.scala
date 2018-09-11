package it.unibo.osmos.redux.ecs.systems.sentientRule

import it.unibo.osmos.redux.ecs.entities.CellBuilder
import it.unibo.osmos.redux.ecs.entities.properties.composed.SentientProperty
import it.unibo.osmos.redux.ecs.systems.borderconditions.{CircularBorder, RectangularBorder}
import it.unibo.osmos.redux.ecs.systems.sentientRule.SentientUtils._
import it.unibo.osmos.redux.mvc.controller.levels.structure.{CollisionRules, Level}
import it.unibo.osmos.redux.mvc.controller.levels.structure.MapShape.{Circle, Rectangle}
import it.unibo.osmos.redux.utils.Constants.Sentient._
import it.unibo.osmos.redux.utils.{Point, Vector}

/** compute the acceleration to avoid the boundary in according with boundary collision rule
  *
  * @param levelInfo level that contain collision rule
  */
case class EscapeFromBoundaryRule(levelInfo: Level) extends SentientRule {

  private val bounceRule = levelInfo.levelMap.mapShape match {
    case shape: Rectangle => RectangularBorder(Point(shape.center.x, shape.center.y), CollisionRules.bouncing, shape.base, shape.height)
    case shape: Circle => CircularBorder(Point(shape.center.x, shape.center.y), CollisionRules.bouncing, shape.radius)
    case _ => throw new IllegalArgumentException
  }

  override def computeRule(sentient: SentientProperty, previousAcceleration: Vector): Vector = {
    escapeFromBoundary(sentient, previousAcceleration)
  }

  //compute the acceleration to avoid the boundary in according with boundary collision rule
  private def escapeFromBoundary(sentient: SentientProperty, previousAcceleration: Vector): Vector = levelInfo.levelMap.collisionRule match {
    case CollisionRules.instantDeath =>
      val actualSpeed = sentient.getSpeedComponent.vector add previousAcceleration
      val sentientCopy = CellBuilder()
        .withPosition(sentient.getPositionComponent)
        .withDimension(sentient.getDimensionComponent.radius + getDesiredSeparation(actualSpeed))
        .withSpeed(actualSpeed.x, actualSpeed.y).buildSentientEntity()
      bounceRule.checkAndSolveCollision(sentientCopy)
      if (sentientCopy.getSpeedComponent.vector == actualSpeed) {
        Vector.zero()
      } else {
        val steer = computeSteer(actualSpeed, sentientCopy.getSpeedComponent.vector normalized())
        steer multiply WeightOfEscapeAccelerationFromBoundary
      }
    case _ => Vector.zero()
  }
}
