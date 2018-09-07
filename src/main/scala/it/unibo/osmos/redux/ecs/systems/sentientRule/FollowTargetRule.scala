package it.unibo.osmos.redux.ecs.systems.sentientRule

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.ecs.entities.properties.composed.{SentientEnemyProperty, SentientProperty}
import it.unibo.osmos.redux.ecs.systems.sentientRule.SentientUtils._
import it.unibo.osmos.redux.utils.Constants.Sentient._
import it.unibo.osmos.redux.utils.{MathUtils, Vector}

import scala.collection.mutable.ListBuffer

/** Rule to compute the acceleration to follow the target enemy
  *
  * @param enemies list of enemies from which choose the target enemy
  */
case class FollowTargetRule(enemies: ListBuffer[SentientEnemyProperty]) extends RuleWithEnemies(enemies) {

  override def computeRule(sentient: SentientProperty, previousAcceleration: Vector): Vector = {
    val escapeVelocity = sentient.getSpeedComponent.vector add previousAcceleration
    findTarget(sentient, enemies, escapeVelocity) match {
      case Some(target) => followTarget(sentient, target, escapeVelocity)
      case _ => Vector.zero()
    }
  }

  /** apply a acceleration to the sentient to follow the target
    *
    * @param sentient sentient entity
    * @param target   target entity
    */
  private def followTarget(sentient: SentientProperty, target: SentientEnemyProperty, actualVelocity: Vector): Vector = {
    val nextPositionTarget = target.getPositionComponent.point.add(target.getSpeedComponent.vector)
    val unitVectorDesiredVelocity = MathUtils.unitVector(nextPositionTarget, sentient.getPositionComponent.point)
    computeSteer(actualVelocity, unitVectorDesiredVelocity)
  }

  /** find the target enemy
    *
    * @param sentient sentient entity
    * @param enemies  list of entity
    * @return the sentient's enemy with greater target coefficient is present, else None
    */
  private def findTarget(sentient: SentientProperty, enemies: ListBuffer[SentientEnemyProperty], escapeVelocity: Vector): Option[SentientEnemyProperty] = {
    enemies.filter(e => e.getCollidableComponent.isCollidable &&
      !(e.getTypeComponent.typeEntity == EntityType.AntiMatter) &&
      sentient.getDimensionComponent.radius > e.getDimensionComponent.radius)
      .map(e => {
        if (hasLostRadiusBehaviour(sentient)) {
          (e, targetCoefficientWithLostRadius(sentient, e, escapeVelocity))
        } else {
          (e, targetCoefficientWithoutLostRadius(sentient, e))
        }
      })
      .filter(e => e._2 > 0) match {
      case list if list.isEmpty => None
      case list => Some(list.max(Ordering.by((d: (SentientEnemyProperty, Double)) => d._2))._1)
    }
  }

  /** compute the coefficient with lost radius
    *
    * @param sentient sentient entity
    * @param enemy    sentient enemy entity
    * @return the coefficient representing the radius that can be gained form an enemy
    */
  private def targetCoefficientWithLostRadius(sentient: SentientProperty, enemy: SentientEnemyProperty, escapeVelocity: Vector): Double = {
    val nextPositionTarget = enemy.getPositionComponent.point.add(enemy.getSpeedComponent.vector)
    val unitVectorDesiredVelocity = MathUtils.unitVector(nextPositionTarget, sentient.getPositionComponent.point)
    val magnitudeOfRotation = computeUnlimitedSteer(escapeVelocity, unitVectorDesiredVelocity).getMagnitude
    val lostRadiusPercentage = magnitudeOfRotation * PercentageOfLostRadiusForMagnitudeAcceleration
    enemy.getDimensionComponent.radius - (sentient.getDimensionComponent.radius * lostRadiusPercentage)
  }

  /** compute the coefficient without lost radius
    *
    * @param sentient sentient entity
    * @param enemy    sentient enemy entity
    * @return a coefficient directly proportional to the enemy's radius and
    *         inversely proportional to the distance between the entities
    */
  private def targetCoefficientWithoutLostRadius(sentient: SentientProperty, enemy: SentientEnemyProperty): Double = {
    enemy.getDimensionComponent.radius / MathUtils.euclideanDistance(sentient.getPositionComponent, enemy.getPositionComponent)
  }
}
