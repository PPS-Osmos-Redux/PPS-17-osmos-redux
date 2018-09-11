package it.unibo.osmos.redux.mvc.view.components.editor

import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityType}
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures._
import it.unibo.osmos.redux.mvc.view.ViewConstants.Window._
import it.unibo.osmos.redux.mvc.view.components.custom.TitledComboBox
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import javafx.scene.input.MouseEvent
import javafx.scene.paint.ImagePattern
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.shape.{Circle, Shape}

/** This class holds those variables and view components responsible to let the user choose and create the entity type in the EditorScene
  *
  * @param editorEntityTypeContainerListener the listener
  */
class EditorEntityTypeContainer(editorEntityTypeContainerListener: EditorEntityTypeContainerListener) {

  /** Entity Type */
  private val entityType: ObjectProperty[EntityType.Value] = ObjectProperty(EntityType.Matter)
  private val entityComboBox = new TitledComboBox[EntityType.Value]("Entity Type:", EntityType.values.toSeq, et => {
    entityType.value = et
  })

  /** Pane containing the field to configure the entities */
  private val cellEntityCreator: CellEntityCreator = new CellEntityCreator
  /** Pane containing the field to configure the gravity entities */
  private val gravityCellEntityCreator: GravityCellEntityCreator = new GravityCellEntityCreator {
    weight.value = 1.0
    visible = false
  }
  /** Pane containing the field to configure the sentient entities */
  private val sentientCellEntityCreator: CellEntityCreator = new SentientCellEntityCreator {
    visible = false
  }
  /** Pane containing the field to configure the player entities */
  private val playerCellEntityCreator: CellEntityCreator = new PlayerCellEntityCreator {
    visible = false
  }

  /** The entity builders */
  private val entityBuilders = Seq(cellEntityCreator, gravityCellEntityCreator, sentientCellEntityCreator, playerCellEntityCreator)

  /** This method returns the currently visible cell entity creator
    *
    * @return the currently visible cell entity creator
    */
  private def getVisibleCellBuilder: CellEntityCreator = entityBuilders.filter((b) => b.visible.value).head

  /** The entity container */
  private val _entityContainer: VBox = new VBox(1.0) {
    margin = Insets(10.0)

    /** Left builder seq */
    private val builderSeq = entityBuilders

    /** Putting the builder one on top of the other */
    private val verticalStackPane = new StackPane() {
      children = builderSeq
      entityType.onChange({
        builderSeq.foreach(cellBuilder => cellBuilder.visible = false)
        entityType.value match {
          case EntityType.Matter => cellEntityCreator.visible = true; cellEntityCreator.entityType_=(EntityType.Matter)
          case EntityType.AntiMatter => cellEntityCreator.visible = true; cellEntityCreator.entityType_=(EntityType.AntiMatter)
          case EntityType.Attractive => gravityCellEntityCreator.visible = true; gravityCellEntityCreator.entityType_=(EntityType.Attractive)
          case EntityType.Repulsive => gravityCellEntityCreator.visible = true; gravityCellEntityCreator.entityType_=(EntityType.Repulsive)
          case EntityType.Sentient => sentientCellEntityCreator.visible = true; sentientCellEntityCreator.entityType_=(EntityType.Sentient)
          case EntityType.Controlled => playerCellEntityCreator.visible = true; playerCellEntityCreator.entityType_=(EntityType.Controlled)
          case _ => cellEntityCreator.visible = true; cellEntityCreator.entityType_=(EntityType.Matter)
        }
      })
    }

    children = List(entityComboBox.root, verticalStackPane)
  }

  /** Getter which returns the entity type container node
    *
    * @return the entity type container node
    */
  def entityContainer: VBox = _entityContainer

  /** The placeholder which follows the user mouse and changes appearance on EntityType change */
  private val _entityPlaceholder: Circle = new Circle() {
    fill.value = new ImagePattern(ImageLoader.getImage(CellTexture))
    radius = getVisibleCellBuilder.radius.value

    /** We set a min and max for the size */
    onScroll = scroll => {
      radius = radius.value + (scroll.getDeltaY / 10) min 150 max 10
      getVisibleCellBuilder.radius.value = radius.value
    }
    /** Showing a different image for different types */
    entityType.onChange(entityType.value match {
      case EntityType.Matter => fill.value = new ImagePattern(ImageLoader.getImage(CellTexture))
      case EntityType.AntiMatter => fill.value = new ImagePattern(ImageLoader.getImage(AntiMatterTexture))
      case EntityType.Attractive => fill.value = new ImagePattern(ImageLoader.getImage(AttractiveTexture))
      case EntityType.Repulsive => fill.value = new ImagePattern(ImageLoader.getImage(RepulsiveTexture))
      case EntityType.Sentient => fill.value = new ImagePattern(ImageLoader.getImage(SentientTexture))
      case EntityType.Controlled => fill.value = new ImagePattern(ImageLoader.getImage(ControllerTexture))
      case _ => fill.value = new ImagePattern(ImageLoader.getImage(CellTexture))
    })
  }

  /** Getter which returns the entity placeholder node
    *
    * @return the entity placeholder node
    */
  def entityPlaceholder: Circle = _entityPlaceholder

  /** This method changes the entity placeholder visibility
    *
    * @param visible true if the entity placeholder must turn visible, false otherwise
    */
  def setEntityPlaceholderVisibility(visible: Boolean) : Unit = _entityPlaceholder.visible.value = visible

  /** This method toggles the entity placeholder visibility */
  def toggleEntityPlaceholder(): Unit = _entityPlaceholder.visible = !_entityPlaceholder.visible.value

  /** This method manages the mouse moved event, updating the placeholder position and the currently visible builder values
    *
    * @param event the mouse event
    */
  def manageMouseMovedEvent(event: MouseEvent): Unit = {
    entityPlaceholder.centerX.value = event.getX
    entityPlaceholder.centerY.value = event.getY
    val visibleBuilder = getVisibleCellBuilder
    visibleBuilder.x.value = event.getX - HalfWindowWidth
    visibleBuilder.y.value = event.getY - HalfWindowHeight
  }

  /** This method manages the mouse clicked event, creating a new shape (visible by the user) and a new entity before calling the listener
    *
    * @param event the mouse event
    */
  def manageMouseClickedEvent(event: MouseEvent): Unit = if (entityPlaceholder.visible.value) {
    /** Creating the new shape which will be shown to the user */
    val newShape: Shape = new Circle {
      fill.value_=(entityPlaceholder.fill.value)
      centerX = entityPlaceholder.centerX.value
      centerY = entityPlaceholder.centerY.value
      radius = entityPlaceholder.radius.value
      effect.value_=(entityPlaceholder.effect.value)
    }
    /** Creating the new editor which will be part of the new level configuration */
    val newCellEntity = getVisibleCellBuilder create()
    /** Notify the listener */
    editorEntityTypeContainerListener.onEntityCreated(newShape, newCellEntity)
  }

}

/**
  * Trait used to notify EditorEntityTypeContainer events
  */
trait EditorEntityTypeContainerListener {

  /** This method tells the listener to update the currently visible entities
    *
    * @param newShape a new enity shape, visible by the user
    * @param newEntity a new built cell entity
    */
  def onEntityCreated(newShape: Shape, newEntity: CellEntity): Unit

}
