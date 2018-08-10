package it.unibo.osmos.redux.main.ecs.components

/**
  * Component for entity able to be collided.
  */
trait CollidableComponent {

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

object CollidableComponent {
  def apply(collidable: Boolean): CollidableComponent = CollidableComponentImpl(collidable)

  private case class CollidableComponentImpl(var _collidable: Boolean) extends CollidableComponent {

    override def isCollidable(): Boolean = _collidable

    override def setCollidable(collidable: Boolean): Unit = _collidable = collidable
  }
}


