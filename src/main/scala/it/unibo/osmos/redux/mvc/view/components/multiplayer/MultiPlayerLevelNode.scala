package it.unibo.osmos.redux.mvc.view.components.multiplayer

import it.unibo.osmos.redux.mvc.controller.LevelInfo
import it.unibo.osmos.redux.mvc.view.components.level.{AnimatedAbstractLevelNode, LevelNodeListener}
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.ImageView
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

/**
  * Level node representing a multiplayer level
  * @param listener the LevelNodeListener
  * @param levelInfo the level info
  */
class MultiPlayerLevelNode(override val listener: LevelNodeListener, override val levelInfo: LevelInfo) extends AnimatedAbstractLevelNode(listener, levelInfo) {

  effect = new DropShadow {
    color = Color.DarkRed
  }

  /* The upper text */
  override lazy val text: Text = new Text() {
    margin = Insets(0, 0, 20, 0)
    style = "-fx-font-size: 12pt"
    text = "Level " + levelInfo.name
  }

  /* The level image */
  override lazy val imageView: ImageView = new ImageView(ImageLoader.getImage("/textures/cell_blue.png")) {
    margin = Insets(20)
  }

  /* The button used to start the level normally */
  override lazy val playButton: Button = new Button("Play") {}

  /* We want to remove the simulation button */
  children.remove(simulationButton)

}
