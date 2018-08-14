package it.unibo.osmos.redux.mvc.view.menus

import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import scalafx.Includes._
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.scene.effect.{DropShadow, SepiaTone}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color

/**
  * This node represents a single selectable level from the menu
  * @param listener the LevelNodeListener
  * @param level the level index
  * @param available true if the level is currently available, false otherwise
  */
class LevelNode(val listener: LevelNodeListener, val level: Int, val available: Boolean) extends VBox {

  alignment = Pos.Center
  padding = Insets(30)

  /* Hover event handlers */
  scaleX <== when(hover) choose 1.2 otherwise 1
  scaleY <== when(hover) choose 1.2 otherwise 1

  /* The level image */
  val imageView: ImageView = new ImageView(ImageLoader.getImage(s"/textures/menu_level_$level.png")) {
    margin = Insets(20)
  }

  /* The button to start the simulation in this level */
  val simulationButton: Button = new Button("Simulation") {
    visible <== when(LevelNode.this.hover) choose true otherwise false
  }

  /* The button to start the level normally */
  val playButton: Button = new Button("Play") {
    visible <== when(LevelNode.this.hover) choose true otherwise false
    alignment = Pos.BottomLeft
  }

  /* We must prevent the user to select unavailable levels */
  if (available) {
    effect = new DropShadow {
      color = Color.Blue
    }
    /* Button handlers */
    simulationButton.onAction = e => listener.onLevelPlayClick(level, simulation = true)
    playButton.onAction = e => listener.onLevelPlayClick(level, simulation = false)
    /* Setting all the components */
    children = Seq(simulationButton, imageView, playButton)
  } else {
    effect = new SepiaTone
    children = imageView
  }

}

/**
  * Trait which gets notified when a LevelNode event occurs
  */
trait LevelNodeListener {

  /**
    * This method gets called when an available level buttons get clicked
    * @param level the level index
    * @param simulation true if the level must be started as a simulation, false otherwise
    */
  def onLevelPlayClick(level: Int, simulation: Boolean)
}
