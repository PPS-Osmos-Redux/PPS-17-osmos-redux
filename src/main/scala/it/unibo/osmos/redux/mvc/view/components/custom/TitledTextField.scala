package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.beans.property.StringProperty
import scalafx.scene.control.TextField

/** A TextField with a Text
  *
  * @param title          the StringProperty representing the title shown
  * @param stringProperty the StringProperty which gets constantly updated
  */
class TitledTextField(override val title: StringProperty, stringProperty: StringProperty) extends TitledNode[TextField](title, vertical = false) {

  /** Secondary constructor which uses a simple String as a title
    *
    * @param title          the String title
    * @param stringProperty the string property
    */
  def this(title: String, stringProperty: StringProperty) {
    this(StringProperty(title), stringProperty)
  }

  /** The node that will be shown after the text
    *
    * @return a node of type N <: Node
    */
  override def innerNode: TextField = new TextField() {
    editable = true
    prefWidth <== maxWidth
    if (stringProperty != null) stringProperty <==> text
  }
}
