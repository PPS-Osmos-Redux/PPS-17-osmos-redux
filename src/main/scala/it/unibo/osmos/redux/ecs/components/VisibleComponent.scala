package it.unibo.osmos.redux.ecs.components

/** Component for visible entity */
trait VisibleComponent extends Component {

  /** @return true if the entity is visible, false otherwise */
  def isVisible: Boolean

  /** Setter. Set the visibility of the entity
    *
    * @param visible visibility police
    */
  def setVisible(visible: Boolean): Unit

  /** Makes a defensive copy of this instance.
    *
    * @return The new instance.
    */
  override def copy(): VisibleComponent = VisibleComponent(isVisible)
}

/** Companion object */
object VisibleComponent {
  def apply(visible: Boolean): VisibleComponent = VisibleComponentImpl(visible)

  private case class VisibleComponentImpl(var _visible: Boolean) extends VisibleComponent {

    override def isVisible: Boolean = _visible

    override def setVisible(visible: Boolean): Unit = _visible = visible
  }

}
