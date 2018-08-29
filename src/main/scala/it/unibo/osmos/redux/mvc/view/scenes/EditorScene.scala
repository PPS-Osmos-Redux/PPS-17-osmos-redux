package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.mvc.model.{MapShape, VictoryRules}
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures._
import it.unibo.osmos.redux.mvc.view.components.custom.TitledComboBox
import it.unibo.osmos.redux.mvc.view.components.editor.{CellEntityBuilder, CircleLevelBuilder, GravityCellEntityBuilder, RectangleLevelBuilder}
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import javafx.scene.paint.ImagePattern
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.control.Button
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
class EditorScene (override val parentStage: Stage, val listener: EditorSceneListener) extends BaseScene(parentStage) {

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
  private var levelType: ObjectProperty[MapShape] = ObjectProperty(MapShape.Circle((400, 400), 400))
  private val levelTypeBox = new TitledComboBox[String]("Level Type:", Seq(MapShape.circle, MapShape.rectangle),{
    case MapShape.circle => levelType.value_=(MapShape.Circle((400, 400), 400))
    case MapShape.rectangle => levelType.value_=(MapShape.Rectangle((400, 400), 400, 400))
    case _ =>
  })

  /** Pane containing the field to configure the circular level */
  private val circularLevelBuilder: CircleLevelBuilder = new CircleLevelBuilder {
    xCenter.value = 800.0
    yCenter.value = 400.0
    radius.value = 300.0
  }
  /** Pane containing the field to configure the rectangular level */
  private val rectangularLevelBuilder: RectangleLevelBuilder = new RectangleLevelBuilder {
    visible = false
    xCenter.value = 500.0
    yCenter.value = 200.0
    levelWidth.value = 600.0
    levelHeight.value = 400.0
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
    println(et)
  })

  /* Pane containing the field to configure the entities*/
  private val cellEntityBuilder: CellEntityBuilder = new CellEntityBuilder
  /* Pane containing the field to configure the gravity entities*/
  private val gravityCellEntityBuilder: GravityCellEntityBuilder = new GravityCellEntityBuilder(isAttractive = true) {
    visible = false
  }


  private val entityTypeContainer: VBox = new VBox(1.0) {

    /** Left builder seq */
    private val builderSeq = Seq(cellEntityBuilder, gravityCellEntityBuilder)

    /** putting the builder one on top of the other */
    private val verticalStackPane = new StackPane() {
      children = builderSeq
      entityType.onChange({
        builderSeq.foreach(cellBuilder => cellBuilder.visible = false)
        entityType.value match {
          case EntityType.Matter => cellEntityBuilder.visible = true; cellEntityBuilder.entityType_=(EntityType.Matter)
          case EntityType.AntiMatter => cellEntityBuilder.visible = true; cellEntityBuilder.entityType_=(EntityType.AntiMatter)
          case EntityType.Attractive => gravityCellEntityBuilder.visible = true; gravityCellEntityBuilder.isAttractive = true
          case EntityType.Repulse => gravityCellEntityBuilder.visible = true; gravityCellEntityBuilder.isAttractive = false
          case EntityType.Sentient => cellEntityBuilder.visible = true; cellEntityBuilder.entityType_=(EntityType.Sentient)
          case EntityType.Controlled => cellEntityBuilder.visible = true; cellEntityBuilder.entityType_=(EntityType.Controlled)
          case _ => cellEntityBuilder.visible = true; cellEntityBuilder.entityType_=(EntityType.Matter)
        }
      })
    }

    children = List(entityComboBox.root, verticalStackPane)
  }

  private val levelTypeContainer: VBox = new VBox(1.0) {

    /** Right builder seq */
    private val builderSeq = Seq(circularLevelBuilder, rectangularLevelBuilder)

    private val verticalStackPane = new StackPane() {
      children = builderSeq
      levelType.onChange({
        builderSeq.foreach(levelBuilder => levelBuilder.visible = false)
        editorElements -= currentLevelPlaceholder
        levelType.value match {
          case _: MapShape.Circle => circularLevelBuilder.visible = true; currentLevelPlaceholder = circularLevelPlaceholder
          case _: MapShape.Rectangle => rectangularLevelBuilder.visible = true; currentLevelPlaceholder = rectangularLevelPlaceholder
          case _ =>
        }
        editorElements += currentLevelPlaceholder
        EditorScene.this.content = editorElements
      })
    }

    children = List(levelTypeBox.root, verticalStackPane)
  }

  private val mainContainer: VBox = new VBox(2.0) {
    children = Seq(victoryRuleBox.root, levelTypeContainer, entityTypeContainer)
  }

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
        case EntityType.Repulse => fill.value = new ImagePattern(ImageLoader.getImage(repulsiveTexture))
        case EntityType.Sentient => fill.value = new ImagePattern(ImageLoader.getImage(sentientTexture))
        case EntityType.Controlled => fill.value = new ImagePattern(ImageLoader.getImage(controllerTexture))
        case _ => fill.value = new ImagePattern(ImageLoader.getImage(cellTexture))
      })

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
    * The currently visible entity placeholder
    */
  var currentEntityPlaceholder: Circle = entityPlaceholder

  /* On control key pressed we hide the placeholder to let the user insert values in the panes */
  onKeyPressed = key => {
    currentEntityPlaceholder.visible = key.isControlDown
  }

  /**
    * On mouse moved, we update the builder
    */
  onMouseMoved = e => {
    currentEntityPlaceholder.centerX.value = e.getX
    currentEntityPlaceholder.centerY.value = e.getY
    cellEntityBuilder.x.value = e.getX
    cellEntityBuilder.y.value = e.getY
  }

  /**
    * On mouse clicked, we parse the placeholder values and created a new element
    */
  onMouseClicked = _ => if (currentEntityPlaceholder.visible.value) {
    editorElements += new Circle {
      fill.value_=(currentEntityPlaceholder.fill.value)
      centerX = currentEntityPlaceholder.centerX.value
      centerY = currentEntityPlaceholder.centerY.value
      radius = currentEntityPlaceholder.radius.value
      effect.value_=(currentEntityPlaceholder.effect.value)
      content = editorElements
    }
  }

  val editorElements = ListBuffer(background, mainContainer, currentEntityPlaceholder, currentLevelPlaceholder)
  content = editorElements

}

/**
  * Trait which gets notified when a EditorScene event occurs
  */
trait EditorSceneListener {


}
