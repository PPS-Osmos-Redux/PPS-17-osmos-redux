package it.unibo.osmos.redux.mvc.view.components.editor

import scalafx.geometry.Insets
import scalafx.scene.layout.VBox

/**
  * Abstract basic EditorComponentBuilder which provide styling and utility methods
  * @tparam A the type of the element provided
  */
abstract class BaseComponentBuilder[A] extends VBox(10.0) with EditorComponentBuilder[A] {

  /* Style */
  padding = Insets(10.0)
  style = "-fx-background-color : #ffffff;"

}

