package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.entities.CellEntity
import it.unibo.osmos.redux.mvc.controller.levels.structure.{CollisionRules, MapShape, VictoryRules}
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures._
import it.unibo.osmos.redux.mvc.view.components.custom.{AlertFactory, StyledButton, TitledComboBox}
import it.unibo.osmos.redux.mvc.view.components.editor._
import it.unibo.osmos.redux.mvc.view.components.instructions.EditorInstructionScreen
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import javafx.scene.input.KeyCode
import scalafx.beans.property.{BooleanProperty, ObjectProperty}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.TextInputDialog
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.ImageView
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.stage.Stage

import scala.collection.mutable.ListBuffer

/** A scene representing a level editor
  *
  * @param parentStage the parent stage
  * @param listener    the EditorSceneListener
  */
//noinspection ForwardReference
class EditorScene(override val parentStage: Stage, val listener: EditorSceneListener, val upperListener: BackClickListener) extends DefaultBackScene(parentStage, upperListener, "Exit Level") {

  /** Entities currently built */
  var builtEntities: ListBuffer[CellEntity] = ListBuffer()

  /** The background image */
  val background: ImageView = new ImageView(ImageLoader.getImage(BackgroundTexture)) {
    fitWidth <== parentStage.width
    fitHeight <== parentStage.height
  }

  /** Boolean binding with the instructionScreen */
  private val instructionScreenVisible: BooleanProperty = BooleanProperty(false)
  /** The instruction screen container */
  private val instructionContainer = new EditorInstructionScreen(this)
  instructionContainer.instructionScreen.visible <== instructionScreenVisible

  /** This method makes the instruction screen appear/disappear */
  private def changeInstructionScreenState(): Unit = {
    instructionScreenVisible.value = !instructionScreenVisible.value
    background.opacity = if (instructionScreenVisible.value) 0.3 else 1.0
  }

  /** Victory Rule */
  private val victoryRule: ObjectProperty[VictoryRules.Value] = ObjectProperty(VictoryRules.becomeTheBiggest)
  private val victoryRuleBox = new TitledComboBox[VictoryRules.Value]("Victory Rule:", VictoryRules.values.toSeq, vr => victoryRule.value = vr)

  /** Collision Rule */
  private val collisionRule: ObjectProperty[CollisionRules.Value] = ObjectProperty(CollisionRules.bouncing)
  private val collisionRuleBox = new TitledComboBox[CollisionRules.Value]("Collision Rule:", CollisionRules.values.toSeq, cr => collisionRule.value = cr)

  /** Level Type, encapsulated in a container with a listener providing reaction to change */
  private val editorLevelTypeContainer = new EditorLevelTypeContainer((oldShape, newShape) => {
    editorElements -= oldShape
    editorElements += newShape
    content = editorElements
  }, !instructionScreenVisible)

  /** Level Type, encapsulated in a container with a listener providing reaction to change and to mouse events */
  private val editorEntityTypeContainer = new EditorEntityTypeContainer((newShape, newEntity) => {
    /** Insert an element to be shown */
    editorElements += newShape

    /** Update the scene content */
    content = editorElements

    /** Insert an entity to the built entities list */
    builtEntities += newEntity
  })

  /** The main container, wrapping the other panes */
  private val mainContainer: BorderPane = new BorderPane() {
    prefWidth <== parentStage.width
    prefHeight <== parentStage.height
    /** We don't show the container if the instructions are visible */
    visible <== !instructionScreenVisible
    left = editorEntityTypeContainer.entityContainer
    right = new VBox(5.0, victoryRuleBox.root, collisionRuleBox.root, editorLevelTypeContainer.levelTypeContainer) {
      margin = Insets(10.0)
      padding = Insets(0.0, 10.0, 0.0, 0.0)
    }
    top = new HBox(20.0, new StyledButton("Save Level") {
      /** We begin the procedure to save the level */
      onAction = _ => saveLevel()
    }, goBack) {
      margin = Insets(10.0)
      alignment = Pos.Center
    }
    center = null
    bottom = new HBox(0.0, new Text("Press [H] to show/hide the instructions") {
      style = "-fx-font-size: 20pt"
      fill = Color.White
      effect = new DropShadow {
        color = Color.Blue
      }
    }) {
      margin = Insets(50.0)
      alignment = Pos.Center
    }
  }

  /** On control key pressed we hide the placeholder to let the user insert values in the panes */
  onKeyPressed = key => key.getCode match {
    case KeyCode.H => changeInstructionScreenState(); editorEntityTypeContainer.setEntityPlaceholderVisibility(false)
    case KeyCode.CONTROL => if (!instructionScreenVisible.value) editorEntityTypeContainer.toggleEntityPlaceholder()
    case _ =>
  }

  /** On mouse moved, we ask the editorEntityTypeContainer to manage the event */
  onMouseMoved = e => editorEntityTypeContainer.manageMouseMovedEvent(e)

  /** On mouse clicked, we ask the editorEntityTypeContainer to manage the event and create a new element */
  onMouseClicked = e => editorEntityTypeContainer.manageMouseClickedEvent(e)

  /** The main editor elements */
  val editorElements: ListBuffer[Node] = ListBuffer(
    background,
    mainContainer,
    editorEntityTypeContainer.entityPlaceholder,
    editorLevelTypeContainer.currentLevelPlaceholder,
    instructionContainer.instructionScreen)
  content = editorElements

  /** Save level procedure */
  private def saveLevel(): Unit = {
    /** We show a confirmation dialog in which we ask for a name */
    val dialog: TextInputDialog = new TextInputDialog("") {
      headerText = "Insert your new level name"
    }
    val levelName = dialog.showAndWait()
    levelName match {
      case Some(name) =>

        /** Check if the name is empty */
        if (name isEmpty) {
          AlertFactory.createErrorAlert("Error", "The level name cannot be empty").showAndWait()
        } else {
          /** The name is valid, we have to retrieve the elements */
          /** Level */
          val level: MapShape = editorLevelTypeContainer.createLevel()
          /** Victory rules */
          val victoryRules = victoryRule.value
          /** Collision rules */
          val collisionRules = collisionRule.value

          /** Save the level */
          listener.onSaveLevel(name, level, victoryRules, collisionRules, builtEntities, {
            /** The level has been created */
            case true => AlertFactory.createConfirmationAlert("Success", "The custom level has been successfully saved").showAndWait()

            /** We show an alert */
            case false => AlertFactory.createErrorAlert("Error", "The custom level could not be made").showAndWait()
          })
        }
      case _ =>
    }
  }

}

/** Trait which gets notified when a EditorScene event occurs */
trait EditorSceneListener {

  /** Called when the user wants to save the level after the name has been chosen
    *
    * @param name           the chosen level name
    * @param map            the chosen level map
    * @param victoryRules   the chosen victory rules
    * @param collisionRules the chosen collision rules
    * @param entities       the inserted entities
    * @param callback       the callback
    */
  def onSaveLevel(name: String, map: MapShape, victoryRules: VictoryRules.Value, collisionRules: CollisionRules.Value, entities: Seq[CellEntity], callback: Boolean => Unit)

}
