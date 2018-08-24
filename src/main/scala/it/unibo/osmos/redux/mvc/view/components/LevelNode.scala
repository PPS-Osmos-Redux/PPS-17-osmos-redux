package it.unibo.osmos.redux.mvc.view.components

import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import scalafx.Includes._
import scalafx.animation.{FadeTransition, Transition}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.scene.effect.{DropShadow, SepiaTone}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.util.Duration

/**
  * This node represents a single selectable level from the menu
  * @param listener the LevelNodeListener
  * @param level the level index
  * @param available true if the level is currently available, false otherwise
  */
class LevelNode(val listener: LevelNodeListener, val level: Int, val available: Boolean) extends VBox {

  alignment = Pos.Center
  padding = Insets(0, 30, 30, 30)

  /* Hover event handlers */
  scaleX <== when(hover) choose 1.2 otherwise 1
  scaleY <== when(hover) choose 1.2 otherwise 1

  /* The upper text */
  val textField: Text = new Text() {
    margin = Insets(0, 0, 20, 0)
    style = "-fx-font-size: 12pt"
    visible = false
  }

  val fadeInTransition: Transition = new FadeTransition(Duration.apply(2000), textField) {
    fromValue = 0.0
    toValue = 1.0
  }

  val fadeOutTransition: Transition = new FadeTransition(Duration.apply(1000), textField) {
    fromValue = 1.0
    toValue = 0.0
    onFinished = _ => textField.visible = false
  }

  /* The level image */
  val imageView: ImageView = new ImageView(ImageLoader.getImage("/textures/" + (level match {
    case 1 => "cell_green.png"
    case 2 => "cell_yellow.png"
    case 3 => "cell_orange.png"
    case 4 => "cell_red.png"
    case 5 => "cell_blue.png"
    case _ => "cell_blue.png"
  }))) {

    margin = Insets(20)
    onMouseEntered = _ => {textField.visible = true; fadeInTransition.play()}
    onMouseExited = _ => {fadeOutTransition.play()}

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
    /* Text */
    textField.text = s"Level $level"
    /* Button handlers */
    simulationButton.onAction = e => listener.onLevelPlayClick(level, simulation = true)
    playButton.onAction = e => listener.onLevelPlayClick(level, simulation = false)
    /* Setting all the components */
    children = Seq(textField, simulationButton, imageView, playButton)
  } else {
    effect = new SepiaTone
    textField.text = "Unlock previous level"
    children = Seq(textField, imageView)
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
