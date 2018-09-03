package it.unibo.osmos.redux.ecs.systems.borderconditions

import it.unibo.osmos.redux.ecs.entities.CollidableProperty
import it.unibo.osmos.redux.mvc.model.CollisionRules
import it.unibo.osmos.redux.utils.Point

/** Abstract class implementing the border collision strategy
  *
  * @param levelCenter center of the level
  */
abstract class AbstractBorder(levelCenter: Point, collisionRule: CollisionRules.Value) {

  private val cellElasticity: Double = 1.0
  private val borderElasticity: Double = 1.0
  protected val restitution: Double = cellElasticity * borderElasticity

  /** Checks if an entity has collided with the border.
    * If so, computes it's new position and speed.
    *
    * @param entity the entity to check
    */
  def checkAndSolveCollision(entity: CollidableProperty): Unit

  /** Checks if an entity is outside of the playable field.
    * If so, repositions it inside the playable field.
    *
    * @param entity the entity to check
    */
  def repositionIfOutsideMap(entity: CollidableProperty): Unit
}
