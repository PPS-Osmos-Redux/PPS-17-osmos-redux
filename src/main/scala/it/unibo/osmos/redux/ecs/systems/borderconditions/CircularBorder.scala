package it.unibo.osmos.redux.ecs.systems.borderconditions

import it.unibo.osmos.redux.ecs.entities.properties.composed.CollidableProperty
import it.unibo.osmos.redux.mvc.controller.levels.structure.CollisionRules
import it.unibo.osmos.redux.utils.{MathUtils, Point, Vector}

/** Collision implementation for a playing field with circular shape */
case class CircularBorder(levelCenter: Point, collisionRule: CollisionRules.Value, levelRadius: Double) extends AbstractBorder(levelCenter, collisionRule) {

  private var maxReachableDistance: Double = _
  private var currentDistanceFromCenter: Double = _

  override protected def initCollisionParameters(entity: CollidableProperty): Unit = {
    super.initCollisionParameters(entity)
    maxReachableDistance = levelRadius - entityRadius
    currentDistanceFromCenter = MathUtils.euclideanDistance(levelCenter, currentPosition)
  }

  override protected def hasCollidedWithBorder: Boolean = {
    currentDistanceFromCenter > maxReachableDistance
  }

  override protected def computeNewPosition(): Point = {
    // For better understanding see: http://gamedev.stackexchange.com/a/29658
    val A = levelCenter
    val B = currentPosition subtract entitySpeed
    val C = currentPosition
    val R = levelRadius
    val r = entityRadius

    val AB = A subtract B
    val BC = B subtract C
    val magnitudeOfAB = AB.getMagnitude
    val magnitudeOfBC = BC.getMagnitude

    if (magnitudeOfBC == 0) {
      C
    } else {
      val b = AB.dot(BC) / Math.pow(magnitudeOfBC, 2) * -1
      val c = (Math.pow(magnitudeOfAB, 2) - Math.pow(R - r, 2)) / Math.pow(magnitudeOfBC, 2)
      val d = b * b - c
      var k = b - Math.sqrt(d)

      if (k < 0) {
        k = b + Math.sqrt(d)
      }

      val BD = C subtract B
      val newMagnitudeOfBD = BD getNewMagnitude (magnitudeOfBC * k)

      // D
      B add newMagnitudeOfBD
    }
  }

  override protected def computeNewSpeed(newPosition: Point): Vector = {
    /* For better understanding see the second answer:
     * https://stackoverflow.com/questions/573084/bounce-angle
     */
    val v = entitySpeed
    val n = newPosition subtract levelCenter normalized()
    val u = n multiply (v dot n)
    val w = v subtract u
    val vAfter = w subtract u
    val reflection = vAfter subtract v multiply restitution
    v add reflection
  }

  override protected def computeNewRadius(): Double = entityRadius - (currentDistanceFromCenter - maxReachableDistance)

  override protected def reposition(): Unit = {
    collisionRule match {
      case CollisionRules.bouncing =>
        val vector = MathUtils.unitVector(levelCenter, currentPosition)
        val back = currentDistanceFromCenter - levelRadius + entityRadius
        positionComponent.point_(currentPosition add (vector multiply back))
      case _ =>
    }
  }
}
