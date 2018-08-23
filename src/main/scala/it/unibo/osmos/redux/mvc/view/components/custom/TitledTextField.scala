package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.beans.property.{DoubleProperty, StringProperty}
import scalafx.scene.control.TextField

class TitledTextField(override val title: String, stringProperty: StringProperty) extends TitledNode[TextField](title, vertical = false) {
  /**
    * The node that will be shown after the text
    *
    * @return a node of type N <: Node
    */
  override def node: TextField = new TextField(){
    editable = true
    prefWidth <== maxWidth
    if (stringProperty != null) stringProperty <== text
  }
}
