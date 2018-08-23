package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.utils.{MathUtils, Point, Vector}

import scala.collection.mutable.ListBuffer

case class SentientSystem() extends AbstractSystemWithTwoTypeOfEntity[SentientProperty, SentientEnemyProperty] {

  private val MAX_SPEED = 2
  private val MAX_ACCELERATION = 0.1
  private val COEFFICIENT_DESIDERED_SEPARATION = 6
  private val radiusThreshold = 4

  override protected def getGroupPropertySecondType: Class[SentientEnemyProperty] = classOf[SentientEnemyProperty]

  override protected def getGroupProperty: Class[SentientProperty] = classOf[SentientProperty]

  override def update(): Unit = entities foreach(sentient => {
    findTarget(sentient, entitiesSecondType) match {
      case Some(target) => followTarget(sentient, target)
      case _ =>
    }
    runAwayFromEnemies(sentient, findEnemies(sentient, entitiesSecondType))
  })

  def followTarget(sentient: SentientProperty, target: SentientEnemyProperty): Unit = {
    //TODO refactor after that add return Point
    val newPositionTarget = target.getPositionComponent.point.add(target.getSpeedComponent.vector)
    val nextPositionTarget = Point(newPositionTarget.x, newPositionTarget.y)
    val desiredVelocity = MathUtils.unitVector(nextPositionTarget, sentient.getPositionComponent.point) multiply MAX_SPEED
    val steer = desiredVelocity subtract sentient.getSpeedComponent.vector limit MAX_ACCELERATION
    applyAcceleration(sentient, steer)
  }

  private def findTarget(sentient: SentientProperty, enemies: ListBuffer[SentientEnemyProperty]): Option[SentientEnemyProperty] =
    enemies.filter(e => !(e.getTypeComponent.typeEntity == EntityType.AntiMatter) &&
                        sentient.getDimensionComponent.radius > e.getDimensionComponent.radius &&
                        e.getDimensionComponent.radius > radiusThreshold)
           .map(e => (e, targetCoefficient(sentient, e)))
           .sortWith((a, b) => a._2 >  b._2 )
           .headOption map (_._1)

  private def targetCoefficient(sentient: SentientProperty, enemy: SentientEnemyProperty): Double =
    enemy.getDimensionComponent.radius / MathUtils.euclideanDistance(sentient.getPositionComponent, enemy.getPositionComponent)

  private def findEnemies(sentient: SentientProperty, enemies: ListBuffer[SentientEnemyProperty]): List[SentientEnemyProperty] =
    enemies.filter(e => e.getTypeComponent.typeEntity == EntityType.AntiMatter ||
                  sentient.getDimensionComponent.radius < e.getDimensionComponent.radius) toList

  private def runAwayFromEnemies(sentient: SentientProperty, enemies: List[SentientEnemyProperty]): Unit = {
    val desideredSeparation = sentient.getDimensionComponent.radius * COEFFICIENT_DESIDERED_SEPARATION
    val steer = average(enemies.map(e => (e, MathUtils.euclideanDistance(sentient.getPositionComponent, e.getPositionComponent)))
           .filter(p => p._2 < desideredSeparation)
           .map(m => MathUtils.unitVector(sentient.getPositionComponent.point, m._1.getPositionComponent.point) divide m._2))
        .normalized() multiply MAX_SPEED subtract sentient.getSpeedComponent.vector limit MAX_ACCELERATION
    applyAcceleration(sentient, steer)
  }

  private def applyAcceleration(sentient: SentientProperty, acceleration: Vector): Unit = {
    val accelerationSentient = sentient.getAccelerationComponent
    accelerationSentient.vector_(accelerationSentient.vector add acceleration)
  }

  def average(s: List[Vector]): Vector = s.foldLeft((Vector.zero(), 0)) ((acc, i) => (acc._1 add i, acc._2 + 1)) match {
    case (_, 0) => Vector.zero()
    case (sum, n) => sum divide n
  }
}
