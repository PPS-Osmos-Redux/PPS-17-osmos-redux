package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.beans.property.{IntegerProperty, StringProperty}
import scalafx.scene.control.TextFormatter.Change
import scalafx.scene.control.{TextField, TextFormatter}
import scalafx.util.converter.{IntStringConverter, NumberStringConverter}

class TitledIntegerField(override val title: StringProperty, private val value: IntegerProperty) extends TitledNode[TextField](title, vertical = false) {

  def this(title: String,value: IntegerProperty) {
    this(StringProperty(title), value)
  }

  /**
    * The maximum value
    */
  var maxValue: Int = Int.MaxValue
  /**
    * The minimum value
    */
  var minValue: Int = Int.MinValue

  /**
    * The node that will be shown after the text
    *
    * @return a node of type N <: Node
    */
  override def innerNode: TextField = new TextField(){
    text.delegate.bindBidirectional(value, new NumberStringConverter)
    editable = true
    prefWidth <== maxWidth
    textFormatter = new TextFormatter[Int](new IntStringConverter, 0, { c: Change => {
      val input = c.getText
      val isNumber = input.matches("\\d+")
      if (!isNumber) c.setText("")
      if (isNumber && (maxValue < c.getControlNewText.toInt || minValue > c.getControlNewText.toInt)) c.setText("")
      c
    }})
  }
}

