package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.{DimensionComponent, PositionComponent, SpawnAction, SpeedComponent}
import it.unibo.osmos.redux.ecs.entities.{SentientEnemyProperty, _}
import it.unibo.osmos.redux.ecs.systems.sentientRule._
import it.unibo.osmos.redux.mvc.model.Level
import it.unibo.osmos.redux.utils.{Point, Vector}

case class SentientSystem(levelInfo: Level) extends AbstractSystemWithTwoTypeOfEntity[SentientProperty, SentientEnemyProperty] {

  private val MAX_ACCELERATION = 0.1
  private val PERCENTAGE_OF_LOST_RADIUS_FOR_MAGNITUDE_ACCELERATION = 0.02
  /**
    * The lost mass spawn point offset (starting from the perimeter of the entity, where to spawn lost mass due to movement)
    */
  private val lostMassSpawnOffset: Double = 0.1

  /**
    * The initial velocity of the lost mass
    */
  private val lostMassInitialVelocity: Double = 4.0

  private var radiusAmount = 0.0

  override protected def getGroupPropertySecondType: Class[SentientEnemyProperty] = classOf[SentientEnemyProperty]

  override protected def getGroupProperty: Class[SentientProperty] = classOf[SentientProperty]

  private val rules: List[SentientRule] = initRules()

  private def initRules(): List[SentientRule] = {
    EscapeFromBoundaryRule(levelInfo) :: EscapeFromEnemiesRule(entitiesSecondType) :: FollowTargetRule(entitiesSecondType) :: Nil
  }

  override def update(): Unit =  entities.filter(e => e.getCollidableComponent.isCollidable)
    .foreach(sentient => {
      var totalAcceleration = rules.head.computeRule(sentient, sentient.getAccelerationComponent.vector)
      rules.tail.foreach(r => {
        totalAcceleration = totalAcceleration add r.computeRule(sentient,totalAcceleration)
      })
      applyAcceleration(sentient, totalAcceleration)
    })

  private def applyAcceleration(sentient: SentientProperty, acceleration: Vector, accelerations: Vector*): Unit = {
    val totalAcceleration = acceleration limit MAX_ACCELERATION
    val accelerationSentient = sentient.getAccelerationComponent
    accelerationSentient.vector_(accelerationSentient.vector add totalAcceleration)

    if (SentientUtils.hasLostRadiusBehaviour(sentient) && totalAcceleration.getMagnitude > 0) {
      val radiusSentient = sentient.getDimensionComponent
      val lostRadiusAmount = radiusSentient.radius * totalAcceleration.getMagnitude * PERCENTAGE_OF_LOST_RADIUS_FOR_MAGNITUDE_ACCELERATION

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
