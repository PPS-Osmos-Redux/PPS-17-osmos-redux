package it.unibo.osmos.redux.ecs.systems.borderconditions

import it.unibo.osmos.redux.ecs.components.{DimensionComponent, PositionComponent, SpeedComponent}
import it.unibo.osmos.redux.ecs.entities.properties.composed.CollidableProperty
import it.unibo.osmos.redux.mvc.controller.levels.structure.CollisionRules
import it.unibo.osmos.redux.utils.{Point, Vector}

/** Abstract class implementing the border collision strategy
  *
  * @param levelCenter   center of the level
  * @param collisionRule the collision rule
  */
abstract class AbstractBorder(levelCenter: Point, collisionRule: CollisionRules.Value) {

  private val cellElasticity: Double = 1.0
  private val borderElasticity: Double = 1.0
  protected val restitution: Double = cellElasticity * borderElasticity

  protected var positionComponent: PositionComponent = _
  protected var currentPosition: Point = _
  protected var speedComponent: SpeedComponent = _
  protected var entitySpeed: Vector = _
  protected var dimensionComponent: DimensionComponent = _
  protected var entityRadius: Double = _

  /** Initializes the current entity's fundamental parameters
    * to perform border collision operations
    *
    * @param entity the current entity
    */
  protected def initCollisionParameters(entity: CollidableProperty): Unit = {
    positionComponent = entity.getPositionComponent
    currentPosition = positionComponent.point
    speedComponent = entity.getSpeedComponent
    entitySpeed = speedComponent.vector
    dimensionComponent = entity.getDimensionComponent
    entityRadius = dimensionComponent.radius
  }

  /** Checks if an entity has collided with the border.
    * If so, computes it's new position and speed.
    *
    * @param entity the entity to check
    */
  def checkAndSolveCollision(entity: CollidableProperty): Unit = {
    initCollisionParameters(entity)
    if (hasCollidedWithBorder) {
      collisionRule match {
        case CollisionRules.bouncing =>
          val newPosition = computeNewPosition()
          positionComponent.point_(newPosition)

          val newSpeed = computeNewSpeed(newPosition)
          speedComponent.vector_(newSpeed)
        case CollisionRules.instantDeath =>
          dimensionComponent.radius_(computeNewRadius())
        case _ => throw new IllegalArgumentException
      }
    }
  }

  /** Checks if an entity is outside of the playable field.
    * If so, repositions it inside the playable field.
    * Used after collision between entities check.
    *
    * @param entity the entity to check
    */
  def checkIfOutsideMap(entity: CollidableProperty): Unit = {
    initCollisionParameters(entity)
    if (hasCollidedWithBorder) {
      reposition()
    }
  }

  /** Returns true if collision with border has happened
    *
    * @return true if collision with border has happened
    */
  protected def hasCollidedWithBorder: Boolean

  /** Takes inside the playable field an entity that was outside
    *
    * @return the entity position after bounce
    */
  protected def computeNewPosition(): Point

  /** Computes the new speed direction after colliding with the border
    *
    * @param newPosition the entity position computed at computeNewPosition()
    * @return the entity speed after bounce
    */
  protected def computeNewSpeed(newPosition: Point): Vector

  /** Returns the entity radius minus the radius portion outside the level borders
    *
    * @return the entity radius after the operations
    */
  protected def computeNewRadius(): Double

  /** Repositions an entity (outside the map) to a legal position
    * as close as possible to the map's edge
    */
  protected def reposition(): Unit
}
