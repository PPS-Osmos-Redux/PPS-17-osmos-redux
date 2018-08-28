package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.mvc.model.{MapShape, VictoryRules}
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures._
import it.unibo.osmos.redux.mvc.view.components.custom.TitledComboBox
import it.unibo.osmos.redux.mvc.view.components.editor.{CellEntityBuilder, GravityCellEntityBuilder}
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import javafx.collections.ObservableList
import javafx.scene.paint.ImagePattern
import scalafx.application.Platform
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Pos
import scalafx.scene.image.ImageView
import scalafx.scene.layout._
import scalafx.scene.shape.{Circle, Shape}
import scalafx.stage.Stage

import scala.collection.mutable

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
  private var levelType: ObjectProperty[MapShape] = ObjectProperty(MapShape.Rectangle((400, 400), 400, 400))
  private val levelTypeBox = new TitledComboBox[String]("Level Type:", Seq(MapShape.circle, MapShape.rectangle),{
    case MapShape.circle => levelType.value_=(MapShape.Circle((400, 400), 400))
    case MapShape.rectangle => levelType.value_=(MapShape.Rectangle((400, 400), 400, 400))
    case _ =>
  })

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
  /* Pane containing the field to configure the entities*/
  private val gravityCellEntityBuilder: GravityCellEntityBuilder = new GravityCellEntityBuilder(isAttractive = true) {
    visible = false
  }


  private val entityTypeContainer: VBox = new VBox(5.0) {

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

  private val levelTypeContainer: VBox = new VBox(10.0) {

    /** Right builder seq */
    private val builderSeq = Seq()

    private val verticalStackPane = new StackPane() {
      children = builderSeq
    }

    children = List(levelTypeBox.root, verticalStackPane)
  }

  private val mainContainer: VBox = new VBox(10.0) {
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
    * The currently visible placeholder, which may be an entity or a level shape
    */
  var currentPlaceholder: Shape = entityPlaceholder

  /* On control key pressed we hide the placeholder to let the user insert values in the panes */
  onKeyPressed = key => {
    currentPlaceholder.visible = key.isControlDown
  }

  /**
    * On mouse moved, we update the builder
    */
  onMouseMoved = e => {
    currentPlaceholder match {
      case c:Circle => {
        c.centerX.value = e.getX
        c.centerY.value = e.getY
        cellEntityBuilder.x.value = e.getX
        cellEntityBuilder.y.value = e.getY
      }
      case _ =>
    }

  }

  /**
    * On mouse clicked, we parse the placeholder values and created a new element
    */
  onMouseClicked = _ => {
    currentPlaceholder match {
      case c:Circle => {
        editorElements += new Circle {
          fill.value_=(c.fill.value)
          centerX = c.centerX.value
          centerY = c.centerY.value
          radius = c.radius.value
          effect.value_=(c.effect.value)
        }
      }
      case _ =>
    }

    content = editorElements
  }

  val editorElements = mutable.MutableList(background, mainContainer, currentPlaceholder)
  content = editorElements
}

/**
  * Trait which gets notified when a EditorScene event occurs
  */
trait EditorSceneListener {


}
