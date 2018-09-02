package it.unibo.osmos.redux.mvc.view.components.level

import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import javafx.event.{ActionEvent, EventHandler}
import scalafx.geometry.Pos
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, Text}
import scalafx.scene.{Node, Scene}

import scala.collection.mutable

/**
  * A simple splash screen shown at the beginning of the level
  */
object LevelScreen {

  /**
    * Builder. The provided scene is used to determine the screen width and height
    *
    * @param parentScene the scene in which the screen will be shown
    */
  case class Builder(private val parentScene: Scene) {

    /**
      * The components which will be added to the screen, in order from top to bottom
      */
    private var components: mutable.ListBuffer[Node] = mutable.ListBuffer()

    /**
      * Add a Text to the screen
      *
      * @param text  the text
      * @param size  the text size
      * @param color the text color
      * @return the builder itself
      */
    def withText(text: String, size: Double, color: Color): Builder = {
      components += new Text(text) {
        font = Font.font("Verdana", size)
        fill = color
      }
      this
    }

    /**
      * Add a Button to the screen
      *
      * @param text    the text
      * @param onClick the handler that will be executed on the button click
      * @return the builder itself
      */
    def withButton(text: String, onClick: EventHandler[ActionEvent]): Builder = {
      components += new StyledButton(text) {
        onAction = onClick
      }
      this
    }

    /**
      * Creates a LevelScreenImpl with the provided components
      *
      * @return a LevelScreenImpl
      */
    def build(): LevelScreenImpl = new LevelScreenImpl(parentScene, components)
  }

  /**
    * A protected class representing a black level screen
    *
    * @param parentScene the parent scene
    * @param components  the screen nodes/components, shown in order top to bottom
    */
  protected class LevelScreenImpl(parentScene: Scene, components: Seq[Node]) extends VBox(spacing = 4) {
    prefWidth <== parentScene.width
    prefHeight <== parentScene.height
    alignment = Pos.Center
    alignmentInParent = Pos.Center
    parentScene fill = Color.Black

    children = components

  }


}


