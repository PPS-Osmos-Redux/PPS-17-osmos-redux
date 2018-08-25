package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.beans.property.StringProperty
import scalafx.event.ActionEvent
import scalafx.scene.control.{TextField, TextFormatter}
import scalafx.scene.control.TextFormatter.Change

class TitledNumericTextField(override val title: StringProperty, stringProperty: StringProperty) extends TitledNode[TextField](title, vertical = false) {

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
    textFormatter = new TextFormatter[String](new TextFormatter[String]((c: Change) => {
      val input = c.getText
      val isNumber = input.matches("[0-9]*")
      if (!isNumber) c.setText(""); c.delegate.setText("")
      c
    }))
    if (stringProperty != null) stringProperty <==> text
  }
}

