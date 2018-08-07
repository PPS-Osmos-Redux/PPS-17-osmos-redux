package it.unibo.osmos.redux.main.ecs.components

/**
  * Component for entity able to be collided.
  */
trait Collidable {

  /**
    *
    * @return true if the entity is able to be collided, false otherwise
    */
  def isCollidable(): Boolean

  /**
    * Setter. Set the ability of the entity to be collided
    * @param collidable ability of the entity
    */
  def setCollidable(collidable: Boolean): Unit
}

object Collidable {
  def apply(collidable: Boolean): Collidable = new CollidableImpl(collidable)

  private case class CollidableImpl(var _collidable: Boolean) extends Collidable {

    override def isCollidable(): Boolean = _collidable

    override def setCollidable(collidable: Boolean): Unit = _collidable = collidable
  }
}


