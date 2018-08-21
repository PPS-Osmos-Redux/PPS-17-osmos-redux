package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.mvc.view.components.TitledComboBox
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import scalafx.scene.image.ImageView
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

  content = Seq(background, entityComboBox)

}

/**
  * Trait which gets notified when a EditorScene event occurs
  */
trait EditorSceneListener {


}
