package it.unibo.osmos.redux.ecs.components

/** Component base trait */
trait Component {

  /** Makes a defensive copy of this instance.
    *
    * @return The new instance.
    */
  def copy(): Component

}
