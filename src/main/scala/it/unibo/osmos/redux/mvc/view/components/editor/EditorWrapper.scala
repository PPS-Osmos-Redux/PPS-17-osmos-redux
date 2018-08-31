package it.unibo.osmos.redux.mvc.view.components.editor

/**
  * Basic trait modelling an element held by a component in the editor
  * @tparam A the element type
  */
trait EditorHolder[A] {

  /**
    * The type held by the EditorHolder. It may be empty.
    */
  var element: Option[A] = Option.empty

}

/**
  * Trait modelling a component which can create an element using a build and store it
  * @tparam A the element type
  */
trait EditorWrapper[A] extends EditorHolder[A] {

  /**
    * Stores the element inside of the holder. The element is built by the chosen EditorComponentBuilder
    * @param editorComponentBuilder the builder which will create the element
    */
  def storeElement(editorComponentBuilder: EditorComponentBuilder[A]): Unit = element = Option.apply(editorComponentBuilder.build())

}

/**
  * A trait modelling a component which can provide an element of the requested type
  * @tparam A the type of the element provided
  */
trait EditorComponentBuilder[A] {

  /**
    * Returns an element of the requested type
    * @return an element of the requeste type
    */
  def build(): A
}
