package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.{DimensionComponent, PositionComponent, SpawnAction, SpeedComponent}
import it.unibo.osmos.redux.ecs.entities.properties.composed.{SentientEnemyProperty, SentientProperty}
import it.unibo.osmos.redux.ecs.systems.sentientRule._
import it.unibo.osmos.redux.mvc.controller.levels.structure.Level
import it.unibo.osmos.redux.utils.Constants.Sentient._
import it.unibo.osmos.redux.utils.{Point, Vector}

/** System that apply sentient rule to each sentient cell */
case class SentientSystem(levelInfo: Level) extends AbstractSystem2[SentientProperty, SentientEnemyProperty] {

  // The lost mass spawn point offset (starting from the perimeter of the entity, where to spawn lost mass due to movement)
  private val LostMassSpawnOffset: Double = 0.1

  // The initial velocity of the lost mass
  private val LostMassInitialVelocity: Double = 4.0

  private var radiusAmount = 0.0

  private val rules: List[SentientRule] = initRules()

  private def initRules(): List[SentientRule] = {
    EscapeFromBoundaryRule(levelInfo) :: EscapeFromEnemiesRule(entitiesSecondType) :: FollowTargetRule(entitiesSecondType) :: Nil
  }

  override def update(): Unit = entities.filter(e => e.getCollidableComponent.isCollidable)
    .foreach(sentient => {
      var totalAcceleration = rules.head.computeRule(sentient, sentient.getAccelerationComponent.vector)
      rules.tail.foreach(r => {
        totalAcceleration = totalAcceleration add r.computeRule(sentient, totalAcceleration)
      })
      applyAcceleration(sentient, totalAcceleration)
    })

  private def applyAcceleration(sentient: SentientProperty, acceleration: Vector, accelerations: Vector*): Unit = {
    val totalAcceleration = acceleration limit MaxAcceleration
    val accelerationSentient = sentient.getAccelerationComponent
    accelerationSentient.vector_(accelerationSentient.vector add totalAcceleration)

    if (SentientUtils.hasLostRadiusBehaviour(sentient) && totalAcceleration.getMagnitude > 0) {
      val radiusSentient = sentient.getDimensionComponent
      val lostRadiusAmount = radiusSentient.radius * totalAcceleration.getMagnitude * PercentageOfLostRadiusForMagnitudeAcceleration

      radiusSentient.radius_(radiusSentient.radius - lostRadiusAmount)
      radiusAmount = radiusAmount + lostRadiusAmount
      if (radiusAmount > 1) {
        // spawn
        val sentientPosition = sentient.getPositionComponent.point
        val directionVector = totalAcceleration multiply -1 normalized()

        val spawnPoint = sentientPosition add (directionVector multiply (radiusSentient.radius + LostMassSpawnOffset + radiusAmount))

        sentient.getSpawnerComponent.enqueueActions(SpawnAction(
          PositionComponent(Point(spawnPoint.x, spawnPoint.y)),
          DimensionComponent(radiusAmount),
          SpeedComponent(directionVector multiply LostMassInitialVelocity)))
        radiusAmount = 0.0
      }
    }
  }
}
