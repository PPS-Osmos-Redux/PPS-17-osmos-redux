package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.geometry.Insets
import scalafx.scene.control.ComboBox
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text

/**
  * A box consisting in a generic combo box and a title
  * @param title the title, shown to the top
  * @param items the items shown in the combobox
  * @param handler the handler called when an item gets selected
  * @tparam A the items type
  */
class TitledComboBox[A](val title: String, val items: Seq[A], val handler: A => Unit) extends VBox(spacing = 4.0) {

  padding = Insets(10.0)
  style = "-fx-background-color : #ffffff;"

  private val text = new Text(title)

  private val comboBox = new ComboBox[A](items)
  comboBox.onAction = _ => handler.apply(comboBox.value.apply())

  children = Seq(text, comboBox)

  comboBox.selectionModel.value.selectFirst()


}
