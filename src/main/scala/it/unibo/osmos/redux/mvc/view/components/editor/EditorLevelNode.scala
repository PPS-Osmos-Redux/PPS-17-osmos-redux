package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.mvc.view.components.level.{AnimatedAbstractLevelNode, LevelNodeListener}
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import scalafx.geometry.Insets
import scalafx.scene.control.Button
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.ImageView
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

/**
  * Trait which gets notified when a LevelNode event occurs
  */
trait EditorLevelNodeListener extends LevelNodeListener{

  /**
    * This method gets called when the user wants to delete a custom level
    * @param level the level index
    */
  def onLevelDeleteClick(level: Int)
}

/**
  * Level node representing a custom level created by the user
  * @param listener the EditorLevelNodeListener
  * @param level the level index
  */
class EditorLevelNode(override val listener: EditorLevelNodeListener, override val level: Int) extends AnimatedAbstractLevelNode(listener, level, playable = true) {

  effect = new DropShadow {
    color = Color.ForestGreen
  }


  /* The upper text */
  override lazy val text: Text = new Text() {
    margin = Insets(0, 0, 20, 0)
    style = "-fx-font-size: 12pt"
    text = s"Level $level"
  }

  /* The level image */
  override lazy val imageView: ImageView = new ImageView(ImageLoader.getImage("/textures/menu_level_5.png")) {
    margin = Insets(20)
  }

  /* The button used to start the simulation in this level */
  override lazy val simulationButton: Button = new Button("Simulation")

  /* The button used to start the level normally */
  override lazy val playButton: Button = new Button("Play") {}

  /* The button used to delete the custom level */
  val deleteButton: Button = new Button("Delete") {
    margin = Insets(20, 0, 0, 0)
    onAction = _ => listener.onLevelDeleteClick(level)
    visible <== EditorLevelNode.this.hover
  }

  children.add(deleteButton)


}
