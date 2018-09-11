package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.beans.property.StringProperty
import scalafx.scene.control.ComboBox

/** A box consisting in a generic combo box and a title
  *
  * @param title the title, shown to the top
  * @param items the items shown in the combobox
  * @param handler the handler called when an item gets selected
  * @tparam A the items type
  */
class TitledComboBox[A](title: String, val items: Seq[A], val handler: A => Unit, val vertical: Boolean = true) extends TitledNode[ComboBox[A]](StringProperty(title), vertical) {

  override def innerNode: ComboBox[A] = new ComboBox[A](items) {

    selectionModel.value.selectFirst()
    onAction = _ => handler.apply(selectionModel.value.getSelectedItem)

  }
}
