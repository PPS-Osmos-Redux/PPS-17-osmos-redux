package it.unibo.osmos.redux.ecs.components

import it.unibo.osmos.redux.utils.Vector

/** Base component with vector element */
trait VectorComponent extends Component {

  /** Return the vector element
    *
    * @return the vector
    */
  def vector: Vector

  /** Set the new vector element
    *
    * @param vector new vector
    */
  def vector_(vector: Vector): Unit
}
