package it.unibo.osmos.redux.main.ecs.components

/**
  * Component for visible entity
  */
trait Visible {

  /**
    *
    * @return true if the entity is visible, false otherwise
    */
  def isVisible(): Boolean

  /**
    * Setter. Set the visibility of the entity
    * @param visible visibility police
    */
  def setVisible(visible: Boolean): Unit
}

object Visible {
  def apply(visible: Boolean): Visible = new VisibleImpl(visible)

  private case class VisibleImpl(var _visible: Boolean) extends Visible {

    override def isVisible(): Boolean = _visible

    override def setVisible(visible: Boolean): Unit = _visible = visible
  }
}
