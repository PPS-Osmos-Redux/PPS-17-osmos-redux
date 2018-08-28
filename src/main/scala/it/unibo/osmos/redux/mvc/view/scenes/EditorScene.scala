package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures._
import it.unibo.osmos.redux.mvc.view.components.custom.TitledComboBox
import it.unibo.osmos.redux.mvc.view.components.editor.CellEntityBuilder
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import javafx.scene.paint.ImagePattern
import scalafx.beans.property.ObjectProperty
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
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
    * Entity Type
    */
  private var entityType: ObjectProperty[EntityType.Value] = ObjectProperty(EntityType.Matter)

  private val entityComboBox = new TitledComboBox[EntityType.Value]("Entity Type:", EntityType.values.toSeq, et => {
    entityType.value = et
    println(et)
  })

  private val cellEntityBuilder = new CellEntityBuilder

  val verticalContainer: VBox = new VBox(10.0, entityComboBox.root, cellEntityBuilder)

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

  var currentPlaceholder: Shape = entityPlaceholder

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

  val editorElements = mutable.MutableList(background, verticalContainer, currentPlaceholder)
  content = editorElements

}

/**
  * Trait which gets notified when a EditorScene event occurs
  */
trait EditorSceneListener {


}
