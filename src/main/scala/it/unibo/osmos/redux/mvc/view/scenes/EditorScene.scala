package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.mvc.view.components.editor.TitledComboBox
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import javafx.scene.paint.ImagePattern
import scalafx.scene.effect.ColorAdjust
import scalafx.scene.image.ImageView
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.stage.Stage

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
  private var entityType: EntityType.Value = EntityType.Matter
  private val entityComboBox = new TitledComboBox[EntityType.Value]("Entity Type", EntityType.values.toSeq, et => {entityType = et; println("val: " + entityType);})

  onMouseClicked = click => {
    val entity: Circle = new Circle(){
      fill.value = new ImagePattern(ImageLoader.getImage("/textures/cell.png"))

      centerX = click.getX
      centerY = click.getY
      radius = 100

      onScroll = scroll => radius = radius.value + (scroll.getDeltaY/10) min 200 max 10

      entityType match {
        case EntityType.Matter => effect = new ColorAdjust(0.0, 0, 0 ,0)
        case EntityType.AntiMatter => effect = new ColorAdjust(0.5, 0, 0 ,0)
        case EntityType.Attractive => effect = new ColorAdjust(0.3, 0.5, -0.8 ,0)
        case EntityType.Repulse => effect = new ColorAdjust(-0.3, 0, 0 ,0)
        case EntityType.Sentient => effect = new ColorAdjust(-0.7, 1,0.3 ,0)
      }
    }

    content.add(entity)
  }

  content = Seq(background, entityComboBox)

}

/**
  * Trait which gets notified when a EditorScene event occurs
  */
trait EditorSceneListener {


}
