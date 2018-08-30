package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.{DimensionComponent, PositionComponent, SpawnAction, SpeedComponent}
import it.unibo.osmos.redux.ecs.entities.{SentientEnemyProperty, _}
import it.unibo.osmos.redux.utils.{MathUtils, Point, Vector}

import scala.collection.mutable.ListBuffer

case class SentientSystem() extends AbstractSystemWithTwoTypeOfEntity[SentientProperty, SentientEnemyProperty] {

  private val MAX_SPEED = 2
  private val MAX_ACCELERATION = 0.1
  private val COEFFICIENT_DESIRED_SEPARATION = 2
  private val MIN_VALUE = 1
  private val PERCENTAGE_OF_LOST_RADIUS_FOR_MAGNITUDE_ACCELERATION = 0.02
  private val WEIGHT_OF_ESCAPE_ACCELERATION = 2
  /**
    * The lost mass spawn point offset (starting from the perimeter of the entity, where to spawn lost mass due to movement)
    */
  val lostMassSpawnOffset: Double = 0.1

  /**
    * The initial velocity of the lost mass
    */
  val lostMassInitialVelocity: Double = 4.0

  var radiusAmount = 0.0

  override protected def getGroupPropertySecondType: Class[SentientEnemyProperty] = classOf[SentientEnemyProperty]

  override protected def getGroupProperty: Class[SentientProperty] = classOf[SentientProperty]

  override def update(): Unit = entities foreach(sentient => {

    val escapeAcceleration = escapeFromEnemies(sentient, findEnemies(sentient, entitiesSecondType))
    val followTargetAcceleration = findTarget(sentient, entitiesSecondType, escapeAcceleration) match {
      case Some(target) => followTarget(sentient, target)
      case _ => Vector.zero()
    }

    applyAcceleration(sentient, escapeAcceleration, followTargetAcceleration)
  })

  /**
    * apply a acceleration to the sentient to follow the target
    * @param sentient sentient entity
    * @param target target entity
    */
  private def followTarget(sentient: SentientProperty, target: SentientEnemyProperty): Vector = {
    val nextPositionTarget = target.getPositionComponent.point.add(target.getSpeedComponent.vector)
    val unitVectorDesiredVelocity = MathUtils.unitVector(nextPositionTarget, sentient.getPositionComponent.point)
    computeSteer(sentient.getSpeedComponent.vector, unitVectorDesiredVelocity)
  }

  /**
    *
    * @param sentient sentient entity
    * @param enemies list of entity
    * @return the sentient's enemy with greater target coefficient is present, else None
    */
  private def findTarget(sentient: SentientProperty, enemies: ListBuffer[SentientEnemyProperty], escapeAcceleration: Vector): Option[SentientEnemyProperty] = {
    val escapeVelocity = sentient.getSpeedComponent.vector add (escapeAcceleration limit MAX_ACCELERATION)
    enemies.filter(e => !(e.getTypeComponent.typeEntity == EntityType.AntiMatter) &&
               sentient.getDimensionComponent.radius > e.getDimensionComponent.radius)
           .map(e => (e, targetCoefficient(sentient, e, escapeVelocity)))
           .filter(e => e._2 > 0) match {
      case list if list.isEmpty => None
      case list => Some(list.max(Ordering.by((d: (SentientEnemyProperty, Double)) => d._2))._1)
    }
  }

  /**
    *
    * @param sentient sentient entity
    * @param enemy sentient enemy entity
    * @return a coefficient directly proportional to the enemy's radius and
    *         inversely proportional to the distance between the entities
    */
  private def targetCoefficient(sentient: SentientProperty, enemy: SentientEnemyProperty, escapeVelocity: Vector): Double = {
    val nextPositionTarget = enemy.getPositionComponent.point.add(enemy.getSpeedComponent.vector)
    val unitVectorDesiredVelocity = MathUtils.unitVector(nextPositionTarget, sentient.getPositionComponent.point)
    val magnitudeOfRotation = computeSteer(escapeVelocity, unitVectorDesiredVelocity).getLength
    val lostRadiusPercentage = magnitudeOfRotation * PERCENTAGE_OF_LOST_RADIUS_FOR_MAGNITUDE_ACCELERATION
    enemy.getDimensionComponent.radius - (sentient.getDimensionComponent.radius * lostRadiusPercentage)
  }


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
    * apply acceleration to run away from all enemies
    * @param sentient sentient entity
    * @param enemies list of enemies
    */
  private def escapeFromEnemies(sentient: SentientProperty, enemies: List[SentientEnemyProperty]): Vector = {
    val desiredSeparation = sentient.getDimensionComponent.radius * COEFFICIENT_DESIRED_SEPARATION
    val filteredEnemies = enemies map(e => (e, computeDistance(sentient, e))) filter(p => p._2 < desiredSeparation)
    shiftDistance(filteredEnemies)
      .map(m => MathUtils.unitVector(sentient.getPositionComponent.point, m._1.getPositionComponent.point) divide m._2)
      .foldLeft((Vector.zero(), 1)) ((acc, i) => (acc._1 add ((i subtract acc._1) divide acc._2), acc._2 + 1))._1 normalized() match {
        case unitVectorDesiredVelocity if unitVectorDesiredVelocity == Vector(0,0) => Vector.zero()
        case unitVectorDesiredVelocity =>
          computeSteer(sentient.getSpeedComponent.vector, unitVectorDesiredVelocity) multiply WEIGHT_OF_ESCAPE_ACCELERATION
      }
  }

  /**
    * if smallest distance(second value of tuple) is less or equal of 0,
    * shift all distance of minus smallest distance plus Double.MinPositiveValue, so
    * the smallest distance is equal to Double.MinPositiveValue
    * @param list list to shift
    * @return shifted list
    */
  private def shiftDistance(list: List[(SentientEnemyProperty, Double)]): List[(SentientEnemyProperty, Double)] = list match {
    case Nil => Nil
    case _ => list.min(Ordering.by((d:(SentientEnemyProperty, Double)) => d._2)) match {
      case min if min._2 <= 1 => list.map(e => (e._1, e._2 - min._2 + MIN_VALUE))
      case _ => list
    }
  }

  private def computeDistance(sentient: SentientProperty, enemy: SentientEnemyProperty): Double = {
    val dist = MathUtils.euclideanDistance(sentient.getPositionComponent, enemy.getPositionComponent)
    dist - sentient.getDimensionComponent.radius - enemy.getDimensionComponent.radius
  }

  private def computeSteer(actualVelocity: Vector, desiredVelocity: Vector): Vector =
    desiredVelocity multiply MAX_SPEED subtract actualVelocity

  private def applyAcceleration(sentient: SentientProperty, acceleration: Vector, accelerations: Vector*): Unit = {
    val totalAcceleration = (acceleration add accelerations.reduce((a1, a2) => a1 add a2)) limit MAX_ACCELERATION
    val accelerationSentient = sentient.getAccelerationComponent
    accelerationSentient.vector_(accelerationSentient.vector add totalAcceleration)
    if (totalAcceleration.getLength > 0) {
      val radiusSentient = sentient.getDimensionComponent
      val lostRadiusAmount = radiusSentient.radius * totalAcceleration.getLength * PERCENTAGE_OF_LOST_RADIUS_FOR_MAGNITUDE_ACCELERATION

      radiusSentient.radius_(radiusSentient.radius - lostRadiusAmount)
      radiusAmount = radiusAmount + lostRadiusAmount
      if (radiusAmount > 1) {
        // spawn
        val sentientPosition = sentient.getPositionComponent.point
        val directionVector = totalAcceleration multiply -1 normalized()

        val spawnPoint = sentientPosition add (directionVector multiply (radiusSentient.radius + lostMassSpawnOffset + radiusAmount))

        sentient.getSpawnerComponent.enqueueActions(SpawnAction(
          PositionComponent(Point(spawnPoint.x, spawnPoint.y)),
          DimensionComponent(radiusAmount),
          SpeedComponent(directionVector.x * lostMassInitialVelocity, directionVector.y * lostMassInitialVelocity)))
        radiusAmount = 0.0
      }
    }
  }
}
