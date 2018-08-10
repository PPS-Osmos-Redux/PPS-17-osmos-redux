package it.unibo.osmos.redux.main.mvc.view.scenes

import it.unibo.osmos.redux.main.mvc.view.ViewConstants.Entities._
import it.unibo.osmos.redux.main.mvc.view.drawables._
import it.unibo.osmos.redux.main.mvc.view.levels.{LevelContext, LevelContextListener}
import it.unibo.osmos.redux.main.utils.MathUtils._
import scalafx.application.Platform
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.stage.Stage

/**
  * This scene holds and manages a single level
  */
class LevelScene(override val parentStage: Stage, val listener: LevelSceneListener) extends BaseScene(parentStage)
  with LevelContextListener {

  /**
    * The canvas which will draw the elements on the screen
    */
  val canvas: Canvas = new Canvas
  val circleDrawable: CircleDrawable = new CircleDrawable(canvas.graphicsContext2D)

  /**
    * The level context, created with the LevelScene. It still needs to be properly setup
    */
  private var _levelContext: Option[LevelContext] = Option.empty

  def levelContext: Option[LevelContext] = _levelContext

  def levelContext_= (levelContext: LevelContext): Unit = _levelContext = Option(levelContext)

  /**
    * OnMouseClicked handler
    */
  onMouseClicked = mouseEvent => {
    levelContext match {
      case Some(lc) => lc pushMouseEvent mouseEvent
      case _ =>
    }
  }

  override def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {

    /* We must draw to the screen the entire collection */
    Platform.runLater({
      canvas.graphicsContext2D.clearRect(parentStage.getX, parentStage.getY, parentStage.getWidth, parentStage.getHeight)
      /* Draw the player */
      //TODO: match types top draw entities differently
      playerEntity match {
        /* The player is present */
        case Some(pe) => calculateColors(defaultEntityMinColor, defaultEntityMaxColor, defaultPlayerColor, pe, entities) foreach(e => circleDrawable.draw(e._1.center, e._1.radius, e._1.radius, e._2))
        /* The player is not present */
        case _ => calculateColors(defaultEntityMinColor, defaultEntityMaxColor, entities) foreach( (e) => circleDrawable.draw(e._1.center, e._1.radius, e._1.radius, e._2))
      }
    })
  }

  /**
    * This method calculates the color of the input entities, interpolating and normalizing it according to the entities size
    * @param minColor the base lower Color
    * @param maxColor the base upper Color
    * @param entities the input entities
    * @return the sequence of pair where the first field is the entity and the second is the color
    */
  private def calculateColors(minColor: Color, maxColor: Color, entities: Seq[DrawableWrapper]): Seq[(DrawableWrapper, Color)] = {
    entities match {
      case Nil => Seq()
      case _ =>
      /* Calculate the min and max radius among the entities */
      val endRadius = getEntitiesExtremeRadiusValues(entities)

      entities map( e => {
        /* Normalize the entity radius */
        val normalizedRadius = normalize(e.radius, endRadius._1, endRadius._2)
        /* Create a pair where the second value is the interpolated color between the two base colors */
        (e, minColor.interpolate(maxColor, normalizedRadius))
      }) seq
    }
  }

  /**
    * This method calculates the color of the input entities, interpolating and normalizing it according to the entities size
    *
    * @param minColor the base lower Color
    * @param maxColor the base upper Color
    * @param playerColor the base player Color
    * @param playerEntity the player entity
    * @param entities the input entities
    * @return the sequence of pair where the first field is the entity and the second is the color
    */
  private def calculateColors(minColor: Color, maxColor: Color, playerColor: Color, playerEntity: DrawableWrapper, entities: Seq[DrawableWrapper]): Seq[(DrawableWrapper, Color)] = {
    entities match {
      case Nil => Seq()
      case _ =>
        /* Calculate the min and max radius among the entities, considering the player */
        val endRadius = getEntitiesExtremeRadiusValues(entities :+ playerEntity)

        entities map {
          /* The entity has the same radius of the player so it will have the same color */
          case e if e.radius == playerEntity.radius => (e, playerColor)
          case e if e.radius < playerEntity.radius =>
            /* The entity is smaller than the player so it's color hue will approach the min one */
            val normalizedRadius = normalize(e.radius, endRadius._1, playerEntity.radius)
            (e, minColor.interpolate(playerColor, normalizedRadius))
          case e =>
            /* The entity is larger than the player so it's color hue will approach the max one */
            val normalizedRadius = normalize(e.radius, playerEntity.radius, endRadius._2)
            (e, playerColor.interpolate(playerColor, normalizedRadius))
        } seq

    }
  }

  /**
    * This method returns a pair consisting of the min and the max radius found in the entities sequence
    * @param entities a DrawableWrapper sequence
    * @return a pair consisting of the min and the max radius found; an IllegalArgumentException on empty sequence
    */
  private def getEntitiesExtremeRadiusValues(entities: Seq[DrawableWrapper]): (Double, Double) = {
    /* Sorting the entities */
    entities.sortWith(_.radius < _.radius)
    /* Retrieving the min and the max radius values */
    entities match {
      case head +: _ :+ tail => (head.radius, tail.radius)
      case _ => throw new IllegalArgumentException("Could not determine the min and max radius from an empty sequence of entities")
    }
  }

}

/**
  * Trait which gets notified when a LevelScene event occurs
  */
trait LevelSceneListener {

}
