package it.unibo.osmos.redux.mvc.view.components.custom

import java.util.Locale

import scalafx.beans.property.{DoubleProperty, StringProperty}
import scalafx.scene.control.TextFormatter.Change
import scalafx.scene.control.{TextField, TextFormatter}
import scalafx.util.converter.{DoubleStringConverter, NumberStringConverter}

/**
  * TextField with a Title which controls the user inputs, checking that the text inserted is a Double
  * @param title the text shown
  * @param value the observable double property
  * @param minValue the minimum acceptable value, set to Double.MinValue if not specified
  * @param maxValue the maximum acceptable value, set to Double.MaxValue if not specified
  */
class TitledDoubleField(override val title: StringProperty, private val value: DoubleProperty, private val minValue: Double = Double.MinValue, private val maxValue: Double = Double.MaxValue)
  extends TitledNode[TextField](title, vertical = false) {

  /**
    * Additional constructor
    * @param title the title
    * @param value the value, as a DoubleProperty
    */
  def this(title: String, value: DoubleProperty) {
    this(StringProperty(title), value)
  }

  /**
    * Additional constructor
    * @param title the title
    * @param value the value, as a DoubleProperty
    * @param minValue the minimum allowed value
    * @param maxValue the maximum allowed value
    */
  def this(title: String, value: DoubleProperty, minValue: Double, maxValue: Double) {
    this(StringProperty(title), value, minValue, maxValue)
  }

  /**
    * The node that will be shown after the text
    *
    * @return a node of type N <: Node
    */
  override def innerNode: TextField = new TextField(){
    text.delegate.bindBidirectional(value, new NumberStringConverter(Locale.US))
    editable = true
    prefWidth <== maxWidth
    textFormatter = new TextFormatter[Double](new DoubleStringConverter(), 0, { c: Change => {
      val input = c.controlNewText
      /** Checking if the double starts with the dot */
      val dotted = input.matches("^(-?)(\\.[0-9]+)?$")
      if (dotted) {
        /** Adding 0 in front*/
        c.setText("0")
        c
      } else {
        /** Checking if its a double of any type */
        val isNumber = input.matches("^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$")
        if (!isNumber) c.setText("")
        if (isNumber && (maxValue <= c.getControlNewText.toDouble || minValue >= c.getControlNewText.toDouble)) c.setText("")
        c
      }
    }})
  }
}
