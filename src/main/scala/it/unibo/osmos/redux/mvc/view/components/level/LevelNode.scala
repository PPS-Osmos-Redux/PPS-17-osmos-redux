package it.unibo.osmos.redux.mvc.view.components.level

import it.unibo.osmos.redux.mvc.controller.LevelInfo.LevelInfo
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
  * Basic abstract level node, consisting of a text, an image, a play button and a simulation button
  * @param listener the LevelNodeListener
  * @param levelInfo the level info
  */
abstract class AbstractLevelNode(val listener: LevelNodeListener, val levelInfo: LevelInfo) extends VBox {

  alignment = Pos.Center
  padding = Insets(0, 30, 30, 30)

  /**
    * Lazy implementation of the basic component. They will be eventually overridden in a not abstract class. This let us implement the objects behaviour
    */
  lazy val imageView: ImageView = new ImageView()
  lazy val text: Text = new Text()
  lazy val playButton: Button = new Button()
  lazy val simulationButton: Button = new Button()

  if (levelInfo.isAvailable) {
    effect = new DropShadow {
      color = Color.Blue
    }

    /**
      * Button handlers, calling the listener
      */
    playButton.onAction = _ => listener.onLevelPlayClick(levelInfo, simulation = false)
    simulationButton.onAction = _ => listener.onLevelPlayClick(levelInfo, simulation = true)

    children = Seq(text, simulationButton, imageView, playButton)
  } else {
    effect = new SepiaTone

    children = Seq(text, imageView)
  }
}

/**
  * Animated version of the base AbstractLevelNode, adding scaling and fading effects
  * @param listener the LevelNodeListener
  * @param levelInfo the level info
  */
abstract class AnimatedAbstractLevelNode(override val listener: LevelNodeListener, override val levelInfo: LevelInfo) extends AbstractLevelNode(listener, levelInfo) {

  /* Hover event handlers */
  scaleX <== when(hover) choose 1.2 otherwise 1
  scaleY <== when(hover) choose 1.2 otherwise 1

  val fadeInTransition: Transition = new FadeTransition(Duration.apply(2000), text) {
    fromValue = 0.0
    toValue = 1.0
  }

  val fadeOutTransition: Transition = new FadeTransition(Duration.apply(1000), text) {
    fromValue = 1.0
    toValue = 0.0
    onFinished = _ => text.visible = false
  }

  text.visible = false

  /**
    * Playing the faind animation whene the image is hovered
    */
  imageView.onMouseEntered = _ => {text.visible = true; fadeInTransition.play()}
  imageView.onMouseExited = _ => {fadeOutTransition.play()}

  playButton.visible <== hover
  simulationButton.visible <== hover

}

/**
  * Trait which gets notified when a LevelNode event occurs
  */
trait LevelNodeListener {

  /**
    * This method gets called when an available level buttons get clicked
    * @param levelInfo the levelInfo name
    * @param simulation true if the level must be started as a simulation, false otherwise
    * @param custom true if the level is a custom one, false otherwise
    */
  def onLevelPlayClick(levelInfo: LevelInfo, simulation: Boolean, custom: Boolean = false)
}


/**
  * This node represents a single selectable level from the menu
  * @param listener the LevelNodeListener
  * @param levelInfo the level info
  * @param index the level index in the selection screen
  */
class LevelNode(override val listener: LevelNodeListener, override val levelInfo: LevelInfo, val index: Int) extends AnimatedAbstractLevelNode(listener, levelInfo) {

  /* The upper text */
  override lazy val text: Text = new Text() {
    margin = Insets(0, 0, 20, 0)
    style = "-fx-font-size: 12pt"
    text = if (levelInfo.isAvailable) "Level " + levelInfo.name else "Unlock previous level"
  }

  /* The level image */
  override lazy val imageView: ImageView = new ImageView(ImageLoader.getImage("/textures/" + (index match {
    case 1 => "cell_green.png"
    case 2 => "cell_yellow.png"
    case 3 => "cell_orange.png"
    case 4 => "cell_red.png"
    case 5 => "cell_blue.png"
    case _ => "cell_blue.png"
  }))) {
    margin = Insets(20)
  }

  /* The button used to start the simulation in this level */
  override lazy val simulationButton: Button = new Button("Simulation")

  /* The button used to start the level normally */
  override lazy val playButton: Button = new Button("Play") {
    alignment = Pos.BottomLeft
  }
}
