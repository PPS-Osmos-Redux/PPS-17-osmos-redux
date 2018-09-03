package it.unibo.osmos.redux.mvc.view.scenes

import scalafx.Includes._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityType}
import it.unibo.osmos.redux.mvc.model.{CollisionRules, MapShape, MapShapeType, VictoryRules}
import it.unibo.osmos.redux.mvc.view.ViewConstants
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures._
import it.unibo.osmos.redux.mvc.view.components.custom.{StyledButton, TitledComboBox}
import it.unibo.osmos.redux.mvc.view.components.editor._
import it.unibo.osmos.redux.mvc.view.components.level.LevelScreen
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import javafx.scene.paint.ImagePattern
import scalafx.beans.property.{BooleanProperty, DoubleProperty, ObjectProperty}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Alert, TextInputDialog}
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle, Shape}
import scalafx.scene.text.Text
import scalafx.stage.Stage

import scala.collection.mutable.ListBuffer

/**
  * A scene representing a level editor
  * @param parentStage the parent stage
  * @param listener the EditorSceneListener
  */
class EditorScene (override val parentStage: Stage, val listener: EditorSceneListener, val upperListener: UpperEditorSceneListener) extends BaseScene(parentStage) {

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
    * Boolean binding with the istructionScreen
    */
  private val instructionScreenVisible: BooleanProperty = BooleanProperty(false)
  /**
    * The instruction screen
    */
  private val instructionScreen = LevelScreen.Builder(this)
    .withText("Instructions", 50, Color.White)
    .withText("Press [Ctrl] to toggle the placeholder visibility", 30, Color.White)
    .withText("Configure the desired entities on the left panel", 30, Color.White)
    .withText("Configure the desired level, vistory rule and collision rule on the right panel", 30, Color.White)
    .withText("When the placeholder is visible, click to insert a new entity on the level", 30, Color.White)
    .withText("Press [i] to show/hide the instructions screen", 20, Color.White)
    .build()
  instructionScreen.visible <== instructionScreenVisible

  private def changeInstructionScreenState(): Unit = {
    instructionScreenVisible.value = !instructionScreenVisible.value
    background.opacity = if (instructionScreenVisible.value) 0.3 else 1.0
  }

  /**
    * Level Type
    */
  private val levelType: ObjectProperty[MapShapeType.Value] = ObjectProperty(MapShapeType.Circle)
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
    levelWidth.value = ViewConstants.Window.defaultWindowWidth / 2
    levelHeight.value = ViewConstants.Window.defaultWindowHeight / 2
    xCenter.value = ViewConstants.Window.defaultWindowWidth / 2
    yCenter.value = ViewConstants.Window.defaultWindowHeight / 2
  }

  /**
    * Level Type
    */
  private val victoryRule: ObjectProperty[VictoryRules.Value] = ObjectProperty(VictoryRules.becomeTheBiggest)
  private val victoryRuleBox = new TitledComboBox[VictoryRules.Value]("Victory Rule:", VictoryRules.values.toSeq, vr => victoryRule.value = vr)

  /**
    * Level Type
    */
  private val collisionRule: ObjectProperty[CollisionRules.Value] = ObjectProperty(CollisionRules.bouncing)
  private val collisionRuleBox = new TitledComboBox[CollisionRules.Value]("Collision Rule:", CollisionRules.values.toSeq, cr => collisionRule.value = cr)

  /**
    * Entity Type
    */
  private val entityType: ObjectProperty[EntityType.Value] = ObjectProperty(EntityType.Matter)
  private val entityComboBox = new TitledComboBox[EntityType.Value]("Entity Type:", EntityType.values.toSeq, et => {
    entityType.value = et
  })

  /* Pane containing the field to configure the entities*/
  private val cellEntityCreator: CellEntityCreator = new CellEntityCreator
  /* Pane containing the field to configure the gravity entities*/
  private val gravityCellEntityCreator: GravityCellEntityCreator = new GravityCellEntityCreator {
    weight.value = 1.0
    visible = false
  }
  /* Pane containing the field to configure the sentient entities*/
  private val sentientCellEntityCreator: CellEntityCreator = new SentientCellEntityCreator {
    visible = false
  }
  /* Pane containing the field to configure the player entities*/
  private val playerCellEntityCreator: CellEntityCreator = new PlayerCellEntityCreator {
    visible = false
  }

  /** The entity builders */
  private val entityBuilders = Seq(cellEntityCreator, gravityCellEntityCreator, sentientCellEntityCreator, playerCellEntityCreator)

  /**
    * Returns the currently visible cell entity creator
    * @return the currently visible cell entity creator
    */
  private def getVisibleCellBuilder: CellEntityCreator = entityBuilders.filter((b) => b.visible.value).head

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
          case EntityType.Matter => cellEntityCreator.visible = true; cellEntityCreator.entityType_=(EntityType.Matter)
          case EntityType.AntiMatter => cellEntityCreator.visible = true; cellEntityCreator.entityType_=(EntityType.AntiMatter)
          case EntityType.Attractive => gravityCellEntityCreator.visible = true; gravityCellEntityCreator.entityType_=(EntityType.Attractive)
          case EntityType.Repulsive => gravityCellEntityCreator.visible = true; gravityCellEntityCreator.entityType_=(EntityType.Repulsive)
          case EntityType.Sentient => sentientCellEntityCreator.visible = true;
          case EntityType.Controlled => playerCellEntityCreator.visible = true; playerCellEntityCreator.entityType_=(EntityType.Controlled)
          case _ => cellEntityCreator.visible = true; cellEntityCreator.entityType_=(EntityType.Matter)
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
    /* We don't show the container if the instructions are visible*/
    visible <== !instructionScreenVisible
    left = entityContainer
    right = new VBox(5.0, victoryRuleBox.root, collisionRuleBox.root, levelTypeContainer) {
      margin = Insets(10.0)
      padding = Insets(0.0, 10.0, 0.0, 0.0)
    }
    top = new HBox(20.0, new StyledButton("Save Level") {
      /** We begin the procedure to save the level */
      onAction = _ => saveLevel()
    }, new StyledButton("Exit Level") {
      /* We go back */
      onAction = _ => upperListener.onExitEditor()
    }){
      margin = Insets(10.0)
      alignment = Pos.Center
    }
    center = null
    bottom = new HBox(0.0, new Text("Press [i] to show/hide the the instructions") {
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
          new Alert(Alert.AlertType.Error) {
            title = "Error"
            contentText.value = "The level name cannot be empty"
          }.showAndWait()
        } else {
          /** The name is valid, we have to retrieve the elements */
          /** Level */
          val level: MapShape = levelType.value match {
            case MapShapeType.Circle => circularLevelBuilder.create()
            case MapShapeType.Rectangle => rectangularLevelBuilder.create()
          }
          /** Victory rules */
          val victoryRules = victoryRule.value
          /** Collision rules */
          val collisionRules = collisionRule.value

          /* Save the level */
          listener.onSaveLevel(name, level, victoryRules, collisionRules, builtEntities, {
            /* The level has been created*/
            case true => new Alert(Alert.AlertType.Confirmation) {
              title = "Success"
              contentText.value = "The custom level has been successfully saved"
            }.showAndWait()
            /* We show an alert */
            case false =>
              new Alert(Alert.AlertType.Error) {
                title = "Error"
                contentText.value = "The custom level could not be made"
              }.showAndWait()
          })
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
    visible <== !instructionScreenVisible
  }

  /**
    * The placeholder which models the rectangular level
    */
  val rectangularLevelPlaceholder: Rectangle = new Rectangle() {
    width <== rectangularLevelBuilder.levelWidth
    height <== rectangularLevelBuilder.levelHeight
    x <== rectangularLevelBuilder.xCenter - rectangularLevelBuilder.levelWidth /2
    y <== rectangularLevelBuilder.yCenter - rectangularLevelBuilder.levelHeight /2
    stroke = Color.White
    strokeWidth = 2.0
    fill = Color.Transparent
    mouseTransparent = true
    visible <== !instructionScreenVisible
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
    radius = getVisibleCellBuilder.radius.value

    /* We set a min and max for the size */
    onScroll = scroll => {
      radius = radius.value + (scroll.getDeltaY/10) min 150 max 10
      getVisibleCellBuilder.radius.value = radius.value
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
  onKeyPressed = key => key.getCode match {
    case KeyCode.I => changeInstructionScreenState(); entityPlaceholder.visible = false
    case KeyCode.CONTROL => if (!instructionScreenVisible.value) entityPlaceholder.visible = !entityPlaceholder.visible.value
    case _ =>
  }

  /**
    * On mouse moved, we update the builder
    */
  onMouseMoved = e => {
    entityPlaceholder.centerX.value = e.getX
    entityPlaceholder.centerY.value = e.getY
    val visibleBuilder = getVisibleCellBuilder
    visibleBuilder.x.value = e.getX
    visibleBuilder.y.value = e.getY
  }

  /**
    * On mouse clicked, we parse the placeholder values and created a new element
    */
  onMouseClicked = _ => if (entityPlaceholder.visible.value) {
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
    val n = getVisibleCellBuilder create()
    println(n)
    builtEntities += n
  }

  /** The main editor elements */
  val editorElements = ListBuffer(background, mainContainer, entityPlaceholder, currentLevelPlaceholder, instructionScreen)
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

  /**
    * Called when the user wants to save the level after the name has been chosen
    * @param name the chosen level name
    * @param map the chosen level map
    * @param victoryRules the chosen victory rules
    * @param collisionRules the chosen collision rules
    * @param entities the inserted entities
    * @param callback the callback
    */
  def onSaveLevel(name: String, map: MapShape, victoryRules: VictoryRules.Value, collisionRules: CollisionRules.Value, entities: Seq[CellEntity], callback: Boolean => Unit)

}
