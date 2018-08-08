package it.unibo.osmos.redux.main.ecs.components

/**
  * Component for visible entity
  */
trait VisibleComponent {

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

object VisibleComponent {
  def apply(visible: Boolean): VisibleComponent = new VisibleComponentImpl(visible)

  private case class VisibleComponentImpl(var _visible: Boolean) extends VisibleComponent {

    override def isVisible(): Boolean = _visible

    override def setVisible(visible: Boolean): Unit = _visible = visible
  }
}
