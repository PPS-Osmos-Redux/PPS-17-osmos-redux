package it.unibo.osmos.redux.mvc.view.components.level

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.mvc.view.ViewConstants
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures._
import it.unibo.osmos.redux.mvc.view.drawables.{CellDrawable, CellWithSpeedDrawable, DrawableWrapper}
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import it.unibo.osmos.redux.utils.MathUtils.normalize
import it.unibo.osmos.redux.utils.Point
import scalafx.application.Platform
import scalafx.scene.canvas.Canvas
import scalafx.scene.image.Image
import scalafx.scene.paint.Color

/** Class which encapsulates all the required methods and procedures to draw the level
  *
  * @param canvas the canvas on which the level entities will be drawn
  */
class LevelDrawer(val canvas: Canvas) {

  /** Level images */
  private object LevelDrawables {
    val cellDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(CellTexture), canvas.graphicsContext2D)
    val playerCellDrawable: CellDrawable = new CellWithSpeedDrawable(ImageLoader.getImage(PlayerCellTexture), canvas.graphicsContext2D)
    val attractiveDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(AttractiveTexture), canvas.graphicsContext2D)
    val repulsiveDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(RepulsiveTexture), canvas.graphicsContext2D)
    val antiMatterDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(AntiMatterTexture), canvas.graphicsContext2D)
    val sentientDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(SentientTexture), canvas.graphicsContext2D)
    val controlledDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(ControllerTexture), canvas.graphicsContext2D)
    val backgroundImage: Image = ImageLoader.getImage(BackgroundTexture)
  }

  /** This method let the drawer calculate the new entities colour and then draws them
    *
    * @param playerEntity the player entity, which may be empty
    * @param entities the entities
    * @param playerCallback the callback called when the player entity gets drawn
    */
  def drawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper], playerCallback: Point => Unit): Unit = {

    var entitiesWrappers: Seq[(DrawableWrapper, Color)] = Seq()

    playerEntity match {
      /** The player is present */
      case Some(pe) => entitiesWrappers = calculateColors(entities, pe)

      /** The player is not present */
      case _ => entitiesWrappers = calculateColorsWithoutPlayer(entities)
    }

    /** We must draw to the screen the entire collection */
    Platform.runLater({
      /** Clear the screen */
      canvas.graphicsContext2D.clearRect(0, 0, canvas.width.value, canvas.height.value)
      canvas.graphicsContext2D.drawImage(LevelDrawables.backgroundImage, 0, 0, canvas.width.value, canvas.height.value)

      /** Draw the entities */
      playerEntity match {
        case Some(pe) => entitiesWrappers foreach (e => e._1 match {
          case `pe` =>
            playerCallback(e._1.center)
            LevelDrawables.playerCellDrawable.draw(e._1, e._2)
          case _ => drawEntity(e._1, e._2)
        })
        case _ => entitiesWrappers foreach (e => drawEntity(e._1, e._2))
      }
    })
  }

  /** This method calculates the color of the input entities, interpolating and normalizing it according to the entities size
    *
    * @param minColor the base lower Color
    * @param maxColor the base upper Color
    * @param entities the input entities
    * @return the sequence of pair where the first field is the entity and the second is the color
    */
  private def calculateColorsWithoutPlayer(entities: Seq[DrawableWrapper], minColor: Color = Color.LightBlue, maxColor: Color = Color.DarkRed): Seq[(DrawableWrapper, Color)] = {
    entities match {
      case Nil => Seq()
      case _ =>

        /** Calculate the min and max radius among the entities */
        val endRadius = getEntitiesExtremeRadiusValues(entities)

        entities map (e => {
          /** Normalize the entity radius */
          val normalizedRadius = normalize(e.radius, endRadius._1, endRadius._2)

          /** Create a pair where the second value is the interpolated color between the two base colors */
          (e, minColor.interpolate(maxColor, normalizedRadius))
        }) seq
    }
  }

  /** This method calculates the color of the input entities when the player is present
    *
    * @param entities     the input entities
    * @param playerEntity the player entity
    * @param minColor     the base lower Color
    * @param maxColor     the base upper Color
    * @param playerColor  the player Color
    * @return the sequence of pair where the first field is the entity and the second is the color
    */
  private def calculateColors(entities: Seq[DrawableWrapper], playerEntity: DrawableWrapper,
                              minColor: Color = ViewConstants.Entities.Colors.DefaultEntityMinColor, maxColor: Color = ViewConstants.Entities.Colors.DefaultEntityMaxColor,
                              playerColor: Color = Color.Green): Seq[(DrawableWrapper, Color)] = {
    entities match {
      case Nil => Seq()
      case _ =>

        /** Calculate the min and max radius among the entities, considering the player */
        entities map {
          case e if e.radius == playerEntity.radius => (e, playerColor)

          /** The entity is smaller than the player so it's color hue will approach the min one */
          case e if e.radius < playerEntity.radius => (e, minColor)

          /** The entity is larger than the player so it's color hue will approach the max one */
          case e => (e, maxColor)
        } seq
    }
  }

  /** Used to draw the correct entity according to its type
    *
    * @param drawableWrapper the drawableWrapper
    * @param color           the border color
    */
  private def drawEntity(drawableWrapper: DrawableWrapper, color: Color): Unit = {
    drawableWrapper.entityType match {
      case EntityType.Attractive => LevelDrawables.attractiveDrawable.draw(drawableWrapper, color)
      case EntityType.Repulsive => LevelDrawables.repulsiveDrawable.draw(drawableWrapper, color)
      case EntityType.AntiMatter => LevelDrawables.antiMatterDrawable.draw(drawableWrapper, color)
      case EntityType.Sentient => LevelDrawables.sentientDrawable.draw(drawableWrapper, color)
      case EntityType.Controlled => LevelDrawables.controlledDrawable.draw(drawableWrapper, color)
      case _ => LevelDrawables.cellDrawable.draw(drawableWrapper, color)
    }
  }

  /** This method returns a pair consisting of the min and the max radius found in the entities sequence
    *
    * @param entities a DrawableWrapper sequence
    * @return a pair consisting of the min and the max radius found; an IllegalArgumentException on empty sequence
    */
  private def getEntitiesExtremeRadiusValues(entities: Seq[DrawableWrapper]): (Double, Double) = {
    /** Sorting the entities */
    val sorted = entities.sortWith(_.radius < _.radius)

    /** Retrieving the min and the max radius values */
    sorted match {
      case head +: _ :+ tail => (head.radius, tail.radius)
      case head +: _ => (head.radius, head.radius)
      case _ => throw new IllegalArgumentException("Could not determine the min and max radius from an empty sequence of entities")
    }
  }
}
