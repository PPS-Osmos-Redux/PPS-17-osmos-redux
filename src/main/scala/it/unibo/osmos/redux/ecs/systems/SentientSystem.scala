package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities._
import it.unibo.osmos.redux.utils.{MathUtils}

import scala.collection.mutable.ListBuffer

case class SentientSystem() extends AbstractSystemWithTwoTypeOfEntity[SentientProperty, SentientEnemyProperty] {

  private val MAX_SPEED = 4
  private val MAX_ACCELERATION = 0.1

  override protected def getGroupPropertySecondType: Class[SentientEnemyProperty] = classOf[SentientEnemyProperty]

  override protected def getGroupProperty: Class[SentientProperty] = classOf[SentientProperty]

  override def update(): Unit = followTarget(entities.head, findTarget(entities.head, entitiesSecondType) get)

  private def followTarget(sentient: SentientProperty, target: SentientEnemyProperty): Unit = {
    val desiredVelocity = MathUtils.unitVector(target.getPositionComponent.point, sentient.getPositionComponent.point).multiply(MAX_SPEED)
    val steer = desiredVelocity.subtract(sentient.getSpeedComponent.vector).limit(MAX_ACCELERATION)
    sentient.getAccelerationComponent.vector_(steer)
  }

  def findTarget(sentient: SentientProperty, enemies: ListBuffer[SentientEnemyProperty]): Option[SentientEnemyProperty] =
    enemies.filter(e => !(e.getTypeComponent.typeEntity == EntityType.AntiMatter) &&
                sentient.getDimensionComponent.radius > e.getDimensionComponent.radius)
           .map(e => (e, targetCoefficient(sentient, e)))
           .sortWith((a, b) => a._2 >  b._2 )
           .headOption map (_._1)

  private def targetCoefficient(sentient: SentientProperty, enemy: SentientEnemyProperty): Double =
    enemy.getDimensionComponent.radius / MathUtils.euclideanDistance(sentient.getPositionComponent, enemy.getPositionComponent)
}
