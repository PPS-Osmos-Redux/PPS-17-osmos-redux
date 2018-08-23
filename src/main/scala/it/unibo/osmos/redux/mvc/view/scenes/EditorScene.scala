package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.mvc.view.components.editor.{CellEntityBuilder, TitledComboBox}
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import javafx.scene.paint.ImagePattern
import scalafx.beans.Observable
import scalafx.beans.property.ObjectProperty
import scalafx.beans.value.ObservableValue
import scalafx.scene.effect.{ColorAdjust, Effect}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Shape}
import scalafx.stage.Stage

import scala.collection.mutable
import scala.tools.nsc.doc.model.Entity

/**
  * A scene representing a level editor
  * @param parentStage the parent stage
  * @param listener the EditorSceneListener
  */
class EditorScene (override val parentStage: Stage, val listener: EditorSceneListener) extends BaseScene(parentStage) {

  /**
    * The background image
    */
  val background: ImageView = new ImageView(ImageLoader.getImage("/textures/background.png")) {
    fitWidth <== parentStage.width
    fitHeight <== parentStage.height
  }

  /**
    * Entity Type
    */
  private var entityType: ObjectProperty[EntityType.Value] = ObjectProperty(EntityType.Matter)

  private val entityComboBox = new TitledComboBox[EntityType.Value]("Entity Type", EntityType.values.toSeq, et => {
    entityType.value = et
    println("val: " + entityType)

  })

  private val cellEntityBuilder = new CellEntityBuilder

  val verticalContainer: VBox = new VBox(10.0, entityComboBox, cellEntityBuilder)


  val entityPlaceholder: Circle = new Circle() {
    fill.value = new ImagePattern(ImageLoader.getImage("/textures/cell_blue.png"))
    radius = 100

    onScroll = scroll => radius = radius.value + (scroll.getDeltaY/10) min 200 max 10

    entityType.onChange(entityType.value match {
        case EntityType.Matter => effect = new ColorAdjust(0.0, 0, 0 ,0)
        case EntityType.AntiMatter => effect = new ColorAdjust(0.5, 0, 0 ,0)
        case EntityType.Attractive => effect = new ColorAdjust(0.3, 0.5, -0.8 ,0)
        case EntityType.Repulse => effect = new ColorAdjust(-0.3, 0, 0 ,0)
        case EntityType.Sentient => effect = new ColorAdjust(-0.7, 1,0.3 ,0)
        case _ =>
      })

  }

  var currentPlaceholder: Shape = entityPlaceholder


  onKeyPressed = key => {
    currentPlaceholder.visible = key.isControlDown
  }

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
