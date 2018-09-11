package it.unibo.osmos.redux.mvc.view.components.editor

/** A trait modelling a component which can provide an element of the requested type
  *
  * @tparam A the type of the element provided
  */
trait EditorCreator[A] {

  /** Returns an element of the requested type
    *
    * @return an element of the requested type
    */
  def create(): A
}
