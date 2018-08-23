package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.scene.control.ComboBox

/**
  * A box consisting in a generic combo box and a title
  * @param title the title, shown to the top
  * @param items the items shown in the combobox
  * @param handler the handler called when an item gets selected
  * @tparam A the items type
  */
class TitledComboBox[A](override val title: String, val items: Seq[A], val handler: A => Unit, val vertical: Boolean = true) extends TitledNode[ComboBox[A]](title, vertical) {

  override def node: ComboBox[A] = new ComboBox[A](items) {

    selectionModel.value.selectFirst()
    onAction = e => handler.apply(selectionModel.value.getSelectedItem)

  }

}
