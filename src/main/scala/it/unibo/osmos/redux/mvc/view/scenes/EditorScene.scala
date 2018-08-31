package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityType}
import it.unibo.osmos.redux.mvc.model.{MapShape, MapShapeType, VictoryRules}
import it.unibo.osmos.redux.mvc.view.ViewConstants
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures._
import it.unibo.osmos.redux.mvc.view.components.custom.{StyledButton, TitledComboBox}
import it.unibo.osmos.redux.mvc.view.components.editor.{CellEntityCreator, CircleLevelCreator, GravityCellEntityCreator, RectangleLevelCreator}
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import javafx.scene.paint.ImagePattern
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Alert, Button, TextInputDialog}
import scalafx.scene.image.ImageView
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle, Shape}
import scalafx.stage.Stage

import scala.collection.mutable.ListBuffer

/**
  * A scene representing a level editor
  * @param parentStage the parent stage
  * @param listener the EditorSceneListener
  */
class EditorScene (override val parentStage: Stage, val listener: EditorSceneListener, val upperListener: UpperLevelSceneListener) extends BaseScene(parentStage) {

  /* Entities currently built */
  var builtEntities: ListBuffer[CellEntity] = ListBuffer()

  /**
    * The background image
    */
  val background: ImageView = new ImageView(ImageLoader.getImage(backgroundTexture)) {
    fitWidth <== parentStage.width
    fitHeight <== parentStage.height
  }

  /**
    * Level Type
    */
  private var levelType: ObjectProperty[MapShapeType.Value] = ObjectProperty(MapShapeType.Circle)
  private val levelTypeBox = new TitledComboBox[MapShapeType.Value]("Level Type:", MapShapeType.values.toSeq, mapType => levelType.value = mapType)

  /** Pane containing the field to configure the circular level */
  private val circularLevelBuilder: CircleLevelCreator = new CircleLevelCreator {
    xCenter.value = ViewConstants.Window.defaultWindowWidth / 2
    yCenter.value = ViewConstants.Window.defaultWindowHeight / 2
    radius.value = 400.0
  }
  /** Pane containing the field to configure the rectangular level */
  private val rectangularLevelBuilder: RectangleLevelCreator = new RectangleLevelCreator {
    visible = false
    levelWidth.value = 800.0
    levelHeight.value = 600.0
    xCenter.value = ViewConstants.Window.defaultWindowWidth / 2 - levelWidth.value / 2
    yCenter.value = ViewConstants.Window.defaultWindowHeight / 2 - levelHeight.value / 2
  }

  /**
    * Level Type
    */
  private var victoryRule: ObjectProperty[VictoryRules.Value] = ObjectProperty(VictoryRules.becomeTheBiggest)
  private val victoryRuleBox = new TitledComboBox[VictoryRules.Value]("Victory Rule:", VictoryRules.values.toSeq, vr => victoryRule.value = vr)

  /**
    * Entity Type
    */
  private var entityType: ObjectProperty[EntityType.Value] = ObjectProperty(EntityType.Matter)
  private val entityComboBox = new TitledComboBox[EntityType.Value]("Entity Type:", EntityType.values.toSeq, et => {
    entityType.value = et
  })

  /* Pane containing the field to configure the entities*/
  private val cellEntityBuilder: CellEntityCreator = new CellEntityCreator
  /* Pane containing the field to configure the gravity entities*/
  private val gravityCellEntityBuilder: GravityCellEntityCreator = new GravityCellEntityCreator(isAttractive = true) {
    visible = false
  }

  /** The entity builders */
  private val entityBuilders = Seq(cellEntityBuilder, gravityCellEntityBuilder)

  /* The entity container*/
  private val entityContainer: VBox = new VBox(1.0) {
    margin = Insets(10.0)

    /** Left builder seq */
    private val builderSeq = entityBuilders

    /** putting the builder one on top of the other */
    private val verticalStackPane = new StackPane() {
      children = builderSeq
      entityType.onChange({
        builderSeq.foreach(cellBuilder => cellBuilder.visible = false)
        entityType.value match {
          case EntityType.Matter => cellEntityBuilder.visible = true; cellEntityBuilder.entityType_=(EntityType.Matter)
          case EntityType.AntiMatter => cellEntityBuilder.visible = true; cellEntityBuilder.entityType_=(EntityType.AntiMatter)
          case EntityType.Attractive => gravityCellEntityBuilder.visible = true; gravityCellEntityBuilder.isAttractive = true
          case EntityType.Repulsive => gravityCellEntityBuilder.visible = true; gravityCellEntityBuilder.isAttractive = false
          case EntityType.Sentient => cellEntityBuilder.visible = true; cellEntityBuilder.entityType_=(EntityType.Sentient)
          case EntityType.Controlled => cellEntityBuilder.visible = true; cellEntityBuilder.entityType_=(EntityType.Controlled)
          case _ => cellEntityBuilder.visible = true; cellEntityBuilder.entityType_=(EntityType.Matter)
        }
      })
    }

    children = List(entityComboBox.root, verticalStackPane)
  }

  /** The level container */
  private val levelTypeContainer: VBox = new VBox(1.0) {

    /** Right builder seq */
    private val builderSeq = Seq(circularLevelBuilder, rectangularLevelBuilder)

    private val verticalStackPane = new StackPane() {
      children = builderSeq
      levelType.onChange({
        builderSeq.foreach(levelBuilder => levelBuilder.visible = false)
        editorElements -= currentLevelPlaceholder
        levelType.value match {
          case MapShapeType.Circle => circularLevelBuilder.visible = true; currentLevelPlaceholder = circularLevelPlaceholder
          case MapShapeType.Rectangle => rectangularLevelBuilder.visible = true; currentLevelPlaceholder = rectangularLevelPlaceholder
          case _ =>
        }
        editorElements += currentLevelPlaceholder
        EditorScene.this.content = editorElements
      })
    }

    children = List(levelTypeBox.root, verticalStackPane)
  }

  /** The main container, wrapping the other panes*/
  private val mainContainer: BorderPane = new BorderPane() {
    prefWidth <== parentStage.width
    prefHeight <== parentStage.height
    left = entityContainer
    right = new VBox(5.0, victoryRuleBox.root, levelTypeContainer) {
      margin = Insets(10.0)
      padding = Insets(0.0, 10.0, 0.0, 0.0)
    }
    top = new HBox(20.0, new StyledButton("Save Level") {
      /** We begin the procedure to save the level */
      onAction = _ => saveLevel()
    }, new StyledButton("Exit Level") {
      /* We go back */
      onAction = _ => upperListener.onStopLevel()
    }) {
      margin = Insets(10.0)
      alignment = Pos.Center
    }
  }

  /** Save level procedure */
  private def saveLevel(): Unit = {
    /* We show a confirmation dialog in which we ask for a name */
    val dialog: TextInputDialog = new TextInputDialog("") {
      headerText = "Insert your new level name"
    }
    val levelName = dialog.showAndWait()
    levelName match {
      case Some(name) =>
        /** Check if the name is empty */
        if (name isEmpty) {
          val alert = new Alert(Alert.AlertType.Error) {
            title = "Error"
            contentText.value = "The level name cannot be empty"
          }
          alert.showAndWait()
        } else {
          /** The name is valid, we have to retrieve the elements */
          /** Level */
          val level = levelType.value match {
            case MapShapeType.Circle => circularLevelBuilder.create()
            case MapShapeType.Rectangle => rectangularLevelBuilder.create()
            case _ =>
          }
          /** Victory rules */
          val victoryRules = victoryRule.value

          /* Save the level */
          //TODO: saveLevel(name: String, level.MapShape.Value, victoryRules: VictoryRules.Value, entities: Seq[CellEntities])
        }

      case _ =>
    }
  }

  /**
    * The placeholder which models the circular level
    */
  val circularLevelPlaceholder: Circle = new Circle() {
    centerX <== circularLevelBuilder.xCenter
    centerY <== circularLevelBuilder.yCenter
    radius <== circularLevelBuilder.radius
    stroke = Color.White
    strokeWidth = 2.0
    fill = Color.Transparent
    mouseTransparent = true
  }

  /**
    * The placeholder which models the rectangular level
    */
  val rectangularLevelPlaceholder: Rectangle = new Rectangle() {
    x <== rectangularLevelBuilder.xCenter
    y <== rectangularLevelBuilder.yCenter
    width <== rectangularLevelBuilder.levelWidth
    height <== rectangularLevelBuilder.levelHeight
    stroke = Color.White
    strokeWidth = 2.0
    fill = Color.Transparent
    mouseTransparent = true
  }

  /**
    * The currently visible level placeholder
    */
  var currentLevelPlaceholder: Shape = circularLevelPlaceholder

  /**
    * The placeholder which follows the user mouse and changes appearance on EntityType change
    */
  val entityPlaceholder: Circle = new Circle() {
    fill.value = new ImagePattern(ImageLoader.getImage(cellTexture))
    radius = 100

    /* We set a min and max for the size */
    onScroll = scroll => {
      radius = radius.value + (scroll.getDeltaY/10) min 150 max 10
      cellEntityBuilder.radius.value = radius.value
    }

    entityType.onChange(entityType.value match {
      case EntityType.Matter => fill.value = new ImagePattern(ImageLoader.getImage(cellTexture))
      case EntityType.AntiMatter => fill.value = new ImagePattern(ImageLoader.getImage(antiMatterTexture))
      case EntityType.Attractive => fill.value = new ImagePattern(ImageLoader.getImage(attractiveTexture))
      case EntityType.Repulsive => fill.value = new ImagePattern(ImageLoader.getImage(repulsiveTexture))
      case EntityType.Sentient => fill.value = new ImagePattern(ImageLoader.getImage(sentientTexture))
      case EntityType.Controlled => fill.value = new ImagePattern(ImageLoader.getImage(controllerTexture))
      case _ => fill.value = new ImagePattern(ImageLoader.getImage(cellTexture))
    })

  }

  /* On control key pressed we hide the placeholder to let the user insert values in the panes */
  onKeyPressed = key => {
    entityPlaceholder.visible = key.isControlDown
  }

  /**
    * On mouse moved, we update the builder
    */
  onMouseMoved = e => {
    entityPlaceholder.centerX.value = e.getX
    entityPlaceholder.centerY.value = e.getY
    cellEntityBuilder.x.value = e.getX
    cellEntityBuilder.y.value = e.getY
  }

  /**
    * On mouse clicked, we parse the placeholder values and created a new element
    */
  onMouseClicked = e => if (entityPlaceholder.visible.value) {
    /** Insert an element to be shown */
    editorElements += new Circle {
      fill.value_=(entityPlaceholder.fill.value)
      centerX = entityPlaceholder.centerX.value
      centerY = entityPlaceholder.centerY.value
      radius = entityPlaceholder.radius.value
      effect.value_=(entityPlaceholder.effect.value)
    }
    content = editorElements

    /** Insert an entity to the built entities list */
    entityBuilders.filter((b) => b.visible.value).head match {
      case gc: GravityCellEntityCreator => builtEntities += gc.create()
      case c: CellEntityCreator => builtEntities += c.create()
      case _ =>
    }

  }

  /** The main editor elements */
  val editorElements = ListBuffer(background, mainContainer, entityPlaceholder, currentLevelPlaceholder)
  content = editorElements

}

/**
  * Trait used by EditorScene to notify an event to the upper scene
  */
trait UpperEditorSceneListener {

  /**
    * Called once when the user quits the editor
    */
  def onExitEditor()
}

/**
  * Trait which gets notified when a EditorScene event occurs
  */
trait EditorSceneListener {


}
