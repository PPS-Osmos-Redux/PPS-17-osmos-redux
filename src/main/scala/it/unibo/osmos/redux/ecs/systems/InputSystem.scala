package it.unibo.osmos.redux.ecs.systems

import it.unibo.osmos.redux.ecs.components.{DimensionComponent, PositionComponent, SpawnAction, SpeedComponent}
import it.unibo.osmos.redux.ecs.entities.properties.composed.InputProperty
import it.unibo.osmos.redux.utils.{InputEventQueue, MathUtils, Point}

/** System managing the player's mouse click inputs */
case class InputSystem() extends AbstractSystem[InputProperty] {

  /** Acceleration coefficient to apply to each input movement */
  private val accelerationCoefficient: Double = 0.8
  /** The lost mass spawn point offset (starting from the
    * entity's perimeter, where to spawn lost mass due to movement)
    */
  private val lostMassSpawnOffset: Double = 0.1
  /** The amount of lost mass for each movement */
  private val lostMassPercentage: Double = 0.05
  /** The initial velocity of the lost mass */
  private val lostMassInitialVelocity: Double = 4.0

  override def update(): Unit = {

    //retrieve all input events
    val inputEvents = InputEventQueue.dequeueAll()

    entities foreach (e => {
      val accel = e.getAccelerationComponent
      val pos = e.getPositionComponent
      val spawner = e.getSpawnerComponent
      val dim = e.getDimensionComponent

      inputEvents filter (_.uuid == e.getUUID) foreach (ev => {

        //compute new acceleration direction
        val directionAcceleration = MathUtils.unitVector(pos.point, ev.point)
        //apply acceleration
        accel.vector_(accel.vector add (directionAcceleration multiply accelerationCoefficient))

        //create a new spawn action
        val loseMassAmount = dim.radius * lostMassPercentage
        val directionVector = MathUtils.unitVector(ev.point, pos.point)
        val spawnPoint = pos.point add (directionVector multiply (dim.radius + lostMassSpawnOffset + loseMassAmount))

        //enqueue spawner action
        spawner.enqueueActions(SpawnAction(
          PositionComponent(Point(spawnPoint.x, spawnPoint.y)),
          DimensionComponent(loseMassAmount),
          SpeedComponent(directionVector multiply lostMassInitialVelocity)))

        //make entity lose mass
        dim.radius_(dim.radius - loseMassAmount / 2)
      })
    })
  }
}
