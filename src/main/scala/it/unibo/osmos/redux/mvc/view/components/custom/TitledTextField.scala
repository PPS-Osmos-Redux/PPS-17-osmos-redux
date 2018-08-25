package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.beans.property.{DoubleProperty, StringProperty}
import scalafx.scene.control.TextField

class TitledTextField(override val title: StringProperty, stringProperty: StringProperty) extends TitledNode[TextField](title, vertical = false) {

  def this(title: String, stringProperty: StringProperty) {
    this(StringProperty(title), stringProperty)
  }

  /**
    * The node that will be shown after the text
    *
    * @return a node of type N <: Node
    */
  override def node: TextField = new TextField(){
    editable = true
    prefWidth <== maxWidth
    if (stringProperty != null) stringProperty <==> text
  }
}
