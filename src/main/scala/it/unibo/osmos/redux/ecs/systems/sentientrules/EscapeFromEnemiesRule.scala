package it.unibo.osmos.redux.ecs.systems.sentientrules

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.ecs.entities.properties.composed.{SentientEnemyProperty, SentientProperty}
import it.unibo.osmos.redux.ecs.systems.sentientrules.SentientUtils._
import it.unibo.osmos.redux.utils.Constants.Sentient._
import it.unibo.osmos.redux.utils.{MathUtils, Vector}

import scala.collection.mutable.ListBuffer

/** Rule to compute the acceleration to run away from enemies
  *
  * @param enemies list of possible enemies
  */
case class EscapeFromEnemiesRule(enemies: ListBuffer[SentientEnemyProperty]) extends SentientRule {

  /*min value of distance between the entity before divide their unitVector for distance,
    so the nearest entities have greater weight*/
  private val minValueOfDistance: Double = 1

  override def computeRule(sentient: SentientProperty, previousAcceleration: Vector): Vector = {
    escapeFromEnemies(sentient, findEnemies(sentient, enemies), previousAcceleration)
  }

  /** search sentient enemies
    *
    * @param sentient sentient entity
    * @param enemies  list of all entities
    * @return list of sentient's enemies
    */
  private def findEnemies(sentient: SentientProperty, enemies: ListBuffer[SentientEnemyProperty]): List[SentientEnemyProperty] =
    enemies.filter(e => e.getCollidableComponent.isCollidable &&
      (e.getTypeComponent.typeEntity == EntityType.AntiMatter ||
        sentient.getDimensionComponent.radius < e.getDimensionComponent.radius)) toList

  /** compute acceleration to run away from all enemies
    *
    * @param sentient sentient entity
    * @param enemies  list of enemies
    */
  private def escapeFromEnemies(sentient: SentientProperty, enemies: List[SentientEnemyProperty], previousAcceleration: Vector): Vector = {
    val actualSpeed = sentient.getSpeedComponent.vector add previousAcceleration
    val desiredSeparation = getDesiredSeparation(actualSpeed)
    enemies.map(e => (e, computeDistance(sentient, e)))
      .filter(p => p._2 < desiredSeparation)
      .shiftDistance(minValueOfDistance)
      .map(m => MathUtils.unitVector(sentient.getPositionComponent.point, m._1.getPositionComponent.point) divide m._2)
      .foldLeft((Vector.zero(), 1))((acc, i) => (acc._1 add ((i subtract acc._1) divide acc._2), acc._2 + 1))._1 normalized() match {
      case unitVectorDesiredVelocity if unitVectorDesiredVelocity == Vector(0, 0) => Vector.zero()
      case unitVectorDesiredVelocity =>
        computeSteer(actualSpeed, unitVectorDesiredVelocity) multiply WeightOfEscapeAccelerationFromEnemies
    }
  }

  private def computeDistance(sentient: SentientProperty, enemy: SentientEnemyProperty): Double = {
    val dist = MathUtils.euclideanDistance(sentient.getPositionComponent, enemy.getPositionComponent)
    dist - sentient.getDimensionComponent.radius - enemy.getDimensionComponent.radius
  }
}
