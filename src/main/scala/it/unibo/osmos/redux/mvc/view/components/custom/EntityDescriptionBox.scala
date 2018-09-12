package it.unibo.osmos.redux.mvc.view.components.custom

import it.unibo.osmos.redux.mvc.view.ViewConstants.Window._
import scalafx.geometry.Pos
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, Text, TextAlignment}


/** View holding a entity image and its description in the game
  *
  * @param image       the entity image
  * @param description the entity description
  */
class EntityDescriptionBox(val image: Image, val description: String) extends HBox(50.0) {
  prefWidth = HalfWindowWidth
  minWidth = HalfWindowWidth
  maxWidth = HalfWindowWidth

  /** The image view */
  private val imageView = new ImageView(image)

  /** The image description text */
  private val text = new Text(description) {
    textAlignment = TextAlignment.Left
    font = Font.font("Verdana", 20.0)
    fill = Color.White
  }

  private val textContainer = new VBox {
    alignment = Pos.Center
    children = text
  }

  children = Seq(imageView, textContainer)
}
