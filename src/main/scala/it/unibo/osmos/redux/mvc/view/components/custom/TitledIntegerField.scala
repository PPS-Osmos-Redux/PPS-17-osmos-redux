package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.beans.property.{IntegerProperty, StringProperty}
import scalafx.scene.control.TextFormatter.Change
import scalafx.scene.control.{TextField, TextFormatter}
import scalafx.util.converter.{IntStringConverter, NumberStringConverter}

/** TextField with a Title which controls the user inputs, checking that the text inserted is an Integer
  *
  * @param title           the text shown
  * @param integerProperty the IntegerProperty which gets constantly updated
  */
class TitledIntegerField(override val title: StringProperty, private val integerProperty: IntegerProperty) extends TitledNode[TextField](title, vertical = false) {

  /**
    * The maximum value
    */
  var maxValue: Int = Int.MaxValue
  /**
    * The minimum value
    */
  var minValue: Int = Int.MinValue

  /** Secondary constructor which uses a simple String as a title
    *
    * @param title           the String title
    * @param integerProperty the IntegerProperty
    */
  def this(title: String, integerProperty: IntegerProperty) {
    this(StringProperty(title), integerProperty)
  }

  /** The node that will be shown after the text
    *
    * @return a node of type N <: Node
    */
  override def innerNode: TextField = new TextField() {
    text.delegate.bindBidirectional(integerProperty, new NumberStringConverter)
    editable = true
    prefWidth <== maxWidth
    textFormatter = new TextFormatter[Int](new IntStringConverter, 0, { c: Change => {
      val input = c.getText
      val isNumber = input.matches("\\d+")
      if (!isNumber) c.setText("")
      if (isNumber && (maxValue < c.getControlNewText.toInt || minValue > c.getControlNewText.toInt)) c.setText("")
      c
    }
    })
  }
}

