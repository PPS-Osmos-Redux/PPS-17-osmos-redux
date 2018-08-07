package it.unibo.osmos.redux.main.mvc.view.scenes


import it.unibo.osmos.redux.main.mvc.view.ViewConstants.Entities.{defaultEntityMaxColor, defaultEntityMinColor}
import it.unibo.osmos.redux.main.mvc.view.drawables.{CircleDrawable, DrawableWrapper}
import it.unibo.osmos.redux.main.mvc.view.levels.{LevelContext, LevelContextListener}
import scalafx.application.Platform
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.stage.Stage

/**
  * This scene holds and manages a single level
  */
class LevelScene(override val parentStage: Stage) extends BaseScene(parentStage)
  with LevelContextListener{

  val canvas: Canvas = new Canvas
  val levelContext: LevelContext = LevelContext(this)
  val circleDrawable: CircleDrawable = new CircleDrawable(canvas.graphicsContext2D)

  onMouseClicked = mouseEvent => levelContext.pushMouseEvent(mouseEvent)

  override def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {

    /* We must draw to the screen the entire collection */
    Platform.runLater({
      canvas.graphicsContext2D.clearRect(parentStage.getX, parentStage.getY, parentStage.getWidth, parentStage.getHeight)
      calculateColors(defaultEntityMinColor, defaultEntityMaxColor, entities) foreach( (e) => circleDrawable.draw(e._1.center, e._1.radius, e._1.radius, e._2))
    })
  }

  /**
    * This method calculates the color of the input entities, interpolating and normalizing it according to the entities size
    * @param minColor the base lower Color
    * @param maxColor the base upper Color
    * @param entities the input entities
    * @return the sequence of pair where the first field is the entity and the second is the color
    */
  private def calculateColors( minColor: Color, maxColor: Color, entities: Seq[DrawableWrapper]): Seq[(DrawableWrapper, Color)] = {
    if (entities.nonEmpty) {
      /* Calculate the min and max radius among the entities */
      /* Sort the list in ascending order */
      //TODO: test functional approach speed
      entities.sortWith(_.radius < _.radius)
      val endRadius: (Double, Double) = entities match {
        case head +:  _ :+ tail => (head.radius, tail.radius)
      }

      entities map( e => {
        /* Normalize the entity radius */
        val normalizedRadius = normalize(e.radius, endRadius._1, endRadius._2)
        /* Create a pair where the second value is the interpolated color between the two base colors */
        (e, minColor.interpolate(maxColor, normalizedRadius))
      }) seq
    } else Seq()

  }

  /**
    * Returns the normalized value of a number between a min and a max
    * @param number the number
    * @param min the min number
    * @param max the max number
    * @return the normalized number between min and max
    */
  private def normalize(number: Double, min: Double, max: Double): Double = (number - min)/(max - min)

}
