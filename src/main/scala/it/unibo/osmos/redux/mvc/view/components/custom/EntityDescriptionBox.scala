package it.unibo.osmos.redux.mvc.view.components.custom

import scalafx.geometry.Pos
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

/** View holding a entity image and its description in the game
  *
  * @param image the entity image
  * @param description the entity description
  */
class EntityDescriptionBox(val image: Image, val description: String) extends HBox(10.0) {

  alignment = Pos.Center
  alignmentInParent = Pos.Center

  private val imageView = new ImageView(image)

  private val text = new Text(description) {
    fill = Color.White
  }

  children = Seq(imageView, text)
}
