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

  /**
    * apply a acceleration to the sentient to follow the target
    * @param sentient sentient entity
    * @param target target entity
    */
  def followTarget(sentient: SentientProperty, target: SentientEnemyProperty): Unit = {
    val nextPositionTarget = target.getPositionComponent.point.add(target.getSpeedComponent.vector)
    val unitVectorDesiredVelocity = MathUtils.unitVector(nextPositionTarget, sentient.getPositionComponent.point)
    val steer = computeSteer(sentient.getSpeedComponent.vector, unitVectorDesiredVelocity)
    applyAcceleration(sentient, steer)
  }

  /**
    *
    * @param sentient sentient entity
    * @param enemies list of entity
    * @return the sentient's enemy with greater target coefficient is present, else None
    */
  private def findTarget(sentient: SentientProperty, enemies: ListBuffer[SentientEnemyProperty]): Option[SentientEnemyProperty] =
    enemies.filter(e => !(e.getTypeComponent.typeEntity == EntityType.AntiMatter) &&
                        sentient.getDimensionComponent.radius > e.getDimensionComponent.radius &&
                        e.getDimensionComponent.radius > radiusThreshold)
           .map(e => (e, targetCoefficient(sentient, e)))
           .sortWith((a, b) => a._2 >  b._2 )
           .headOption map (_._1)

  /**
    *
    * @param sentient sentient entity
    * @param enemy sentient enemy entity
    * @return a coefficient directly proportional to the enemy's radius and
    *         inversely proportional to the distance between the entities
    */
  private def targetCoefficient(sentient: SentientProperty, enemy: SentientEnemyProperty): Double =
    enemy.getDimensionComponent.radius / MathUtils.euclideanDistance(sentient.getPositionComponent, enemy.getPositionComponent)

  /**
    * search sentient enemies
    * @param sentient sentient entity
    * @param enemies list of all entities
    * @return list of sentient's enemies
    */
  private def findEnemies(sentient: SentientProperty, enemies: ListBuffer[SentientEnemyProperty]): List[SentientEnemyProperty] =
    enemies.filter(e => e.getTypeComponent.typeEntity == EntityType.AntiMatter ||
                  sentient.getDimensionComponent.radius < e.getDimensionComponent.radius) toList

  /**
    * applay acceleration to run away from all enemies
    * @param sentient sentient entity
    * @param enemies list of enemies
    */
  private def runAwayFromEnemies(sentient: SentientProperty, enemies: List[SentientEnemyProperty]): Unit = {
    val desideredSeparation = sentient.getDimensionComponent.radius * COEFFICIENT_DESIDERED_SEPARATION
    val unitVectorDesiredVelocity = enemies.map(e => (e, MathUtils.euclideanDistance(sentient.getPositionComponent, e.getPositionComponent)))
          .filter(p => p._2 < desideredSeparation)
          .map(m => MathUtils.unitVector(sentient.getPositionComponent.point, m._1.getPositionComponent.point) divide m._2)
          .foldLeft((Vector.zero(), 1)) ((acc, i) => (acc._1 add ((i subtract acc._1) divide acc._2), acc._2 + 1))._1 normalized()
    val steer = computeSteer(sentient.getSpeedComponent.vector, unitVectorDesiredVelocity)
    applyAcceleration(sentient, steer)
  }

  private def computeSteer(actualVelocity: Vector, desideredVelovity: Vector): Vector =
    desideredVelovity multiply MAX_SPEED subtract actualVelocity limit MAX_ACCELERATION

  private def applyAcceleration(sentient: SentientProperty, acceleration: Vector): Unit = {
    val accelerationSentient = sentient.getAccelerationComponent
    accelerationSentient.vector_(accelerationSentient.vector add acceleration)
  }
}
