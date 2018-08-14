package it.unibo.osmos.redux.mvc.view.menus

import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.image.ImageView
import scalafx.scene.layout.Pane

/**
  * This node represents a single selectable level from the menu
  * @param requestedWidth the node width
  * @param requestedHeight the node height
  */
class LevelNode(val requestedWidth: Double, val requestedHeight: Double, val level: Int, val levelVisible: Boolean) extends Pane {

  padding = Insets(20)

  val imageView: ImageView = new ImageView(ImageLoader.getImage(s"/textures/menu_level_$level.png")) {
    fitWidth = requestedWidth
    fitHeight = requestedHeight
  }

  translateY = - this.height.get() / 2

  val simulationButton: Button = new Button("Simulation") {
    visible <== when(LevelNode.this.hover) choose true otherwise false
  }

  children = Seq(imageView, simulationButton)

}
