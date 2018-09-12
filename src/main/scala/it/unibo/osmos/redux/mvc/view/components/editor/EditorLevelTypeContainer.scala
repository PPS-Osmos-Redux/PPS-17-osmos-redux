package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.mvc.controller.levels.structure.{MapShape, MapShapeType}
import it.unibo.osmos.redux.mvc.view.ViewConstants.Editor.{StartingLevelHeight, StartingLevelRadius, StartingLevelWidth}
import it.unibo.osmos.redux.mvc.view.ViewConstants.Window._
import it.unibo.osmos.redux.mvc.view.components.custom.TitledComboBox
import javafx.beans.binding.BooleanBinding
import scalafx.beans.property.ObjectProperty
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle, Shape}

/** This class holds those variables and view components responsible to let the user choose the level type in the EditorScene
  *
  * @param editorLevelTypeContainerListener the listener
  * @param isContainerVisible               BooleanBinding which manages the panels visibility
  */
class EditorLevelTypeContainer(editorLevelTypeContainerListener: EditorLevelTypeContainerListener, isContainerVisible: BooleanBinding) {

  /** Level Type */
  private val levelType: ObjectProperty[MapShapeType.Value] = ObjectProperty(MapShapeType.Circle)
  private val levelTypeBox = new TitledComboBox[MapShapeType.Value]("Level Type:", MapShapeType.values.toSeq, mapType => levelType.value = mapType)

  /** Pane containing the field to configure the circular level */
  private val circularLevelBuilder: CircleLevelCreator = new CircleLevelCreator {
    xCenter.value = 0.0
    yCenter.value = 0.0
    radius.value = StartingLevelRadius
  }
  /** Pane containing the field to configure the rectangular level */
  private val rectangularLevelBuilder: RectangleLevelCreator = new RectangleLevelCreator {
    visible = false
    levelWidth.value = StartingLevelWidth
    levelHeight.value = StartingLevelHeight
    xCenter.value = 0.0
    yCenter.value = 0.0
  }

  /** The level container node */
  private val _levelTypeContainer: VBox = new VBox(1.0) {

    /* Adding the two builders */
    private val builderSeq = Seq(circularLevelBuilder, rectangularLevelBuilder)

    private val verticalStackPane = new StackPane() {
      children = builderSeq
      /** Reacting to change */
      levelType.onChange({
        builderSeq.foreach(levelBuilder => levelBuilder.visible = false)
        val oldShape = currentLevelPlaceholder
        levelType.value match {
          case MapShapeType.Circle => circularLevelBuilder.visible = true; currentLevelPlaceholder = circularLevelPlaceholder
          case MapShapeType.Rectangle => rectangularLevelBuilder.visible = true; currentLevelPlaceholder = rectangularLevelPlaceholder
          case _ =>
        }
        val newShape = currentLevelPlaceholder
        editorLevelTypeContainerListener.updatePlaceholder(oldShape, newShape)
      })
    }

    children = List(levelTypeBox.root, verticalStackPane)
  }
  /** The placeholder which models the circular level */
  private val circularLevelPlaceholder: Circle = new Circle() {
    centerX <== circularLevelBuilder.xCenter + HalfWindowWidth
    centerY <== circularLevelBuilder.yCenter + HalfWindowHeight
    radius <== circularLevelBuilder.radius
    stroke = Color.White
    strokeWidth = 2.0
    fill = Color.Transparent
    mouseTransparent = true
    visible <== isContainerVisible
  }
  /** The placeholder which models the rectangular level */
  private val rectangularLevelPlaceholder: Rectangle = new Rectangle() {
    width <== rectangularLevelBuilder.levelWidth
    height <== rectangularLevelBuilder.levelHeight
    x <== rectangularLevelBuilder.xCenter - rectangularLevelBuilder.levelWidth / 2 + HalfWindowWidth
    y <== rectangularLevelBuilder.yCenter - rectangularLevelBuilder.levelHeight / 2 + HalfWindowHeight
    stroke = Color.White
    strokeWidth = 2.0
    fill = Color.Transparent
    mouseTransparent = true
    visible <== isContainerVisible
  }
  /** The currently visible level placeholder */
  var currentLevelPlaceholder: Shape = circularLevelPlaceholder

  /** Getter which returns the level type container node
    *
    * @return the level type container node
    */
  def levelTypeContainer: VBox = _levelTypeContainer

  /** This method creates a MapShape based on the currently selected level type
    *
    * @return the currently selected MapShape
    */
  def createLevel(): MapShape = levelType.value match {
    case MapShapeType.Circle => circularLevelBuilder.create()
    case MapShapeType.Rectangle => rectangularLevelBuilder.create()
  }
}

/**
  * Trait used to notify EditorLevelTypeContainer events
  */
trait EditorLevelTypeContainerListener {

  /** This method tells the listener to update the placeholder
    *
    * @param oldShape the old placeholder shape
    * @param newShape the new placeholder shape
    */
  def updatePlaceholder(oldShape: Shape, newShape: Shape)
}
