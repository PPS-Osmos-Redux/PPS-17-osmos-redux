package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities._
import it.unibo.osmos.redux.mvc.view.components.{LevelStateBox, LevelStateBoxListener}
import it.unibo.osmos.redux.mvc.view.drawables._
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.mvc.view.levels.{LevelContext, LevelContextListener}
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import it.unibo.osmos.redux.utils.MathUtils._
import it.unibo.osmos.redux.utils.Point
import scalafx.animation.FadeTransition
import scalafx.application.Platform
import scalafx.geometry.Pos
import scalafx.scene.canvas.Canvas
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.scene.text.{Font, Text}
import scalafx.stage.Stage
import scalafx.util.Duration

/**
  * This scene holds and manages a single level
  */
class LevelScene(override val parentStage: Stage, val listener: LevelSceneListener) extends BaseScene(parentStage)
  with LevelContextListener with LevelStateBoxListener {

  /**
    * The canvas which will draw the elements on the screen
    */
  val canvas: Canvas = new Canvas(parentStage.getWidth, parentStage.getHeight) {
    width <== parentStage.width
    height <== parentStage.height
    cache = true
    opacity = 0.0
  }

  /**
    * The screen showed when the game is paused
    */
  val pauseScreen : VBox = new VBox(){
    prefWidth <== parentStage.width
    prefHeight <== parentStage.height
    alignment = Pos.Center
    visible = false

    children = Seq(new Text("Game paused") {
      font = Font.font("Verdana", 20)
      fill = Color.White
    })
  }

  /**
    * The splash screen showed when the game is paused
    */
  val splashScreen : VBox = new VBox(){
    prefWidth <== parentStage.width
    prefHeight <== parentStage.height
    alignment = Pos.Center
    fill = Color.Black

    children = Seq(new Text("Become the opposite of small") {
      font = Font.font("Verdana", 40)
      fill = Color.White
    })

  }

  /* We start the level */
  def startLevel(): Unit = {
    /* The level gets immediately stopped */
    listener.onPauseLevel()
    /* Splash screen animation, starting with a FadeIn */
    new FadeTransition(Duration.apply(2000), splashScreen) {
      fromValue = 0.0
      toValue = 1.0
      autoReverse = true
      /* FadeOut */
      onFinished = _ => new FadeTransition(Duration.apply(1000), splashScreen) {
        fromValue = 1.0
        toValue = 0.0
        autoReverse = true
        /* Showing the canvas */
        onFinished = _ => new FadeTransition(Duration.apply(3000), canvas) {
          fromValue = 0.0
          toValue = 1.0
          /* Removing the splash screen to reduce the load. Then the level is resumed */
          onFinished = _ => content.remove(splashScreen); listener.onResumeLevel()
        }.play()
      }.play()
    }.play()
  }

  /**
    * The images used to draw cells, background and level
    */
  val cellDrawable: CellTintDrawable = new CellTintDrawable(ImageLoader.getImage("/textures/cell.png"), canvas.graphicsContext2D)
  val backgroundDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage("/textures/background.png"), canvas.graphicsContext2D)
  var mapDrawable: Option[StaticImageDrawable] = Option.empty

  /**
    * The content of the whole scene
    */
  content = Seq(canvas, pauseScreen, new LevelStateBox(this,4.0), splashScreen)

  /**
    * The level context, created with the LevelScene. It still needs to be properly setup
    */
  private var _levelContext: Option[LevelContext] = Option.empty

  def levelContext: Option[LevelContext] = _levelContext

  def levelContext_= (levelContext: LevelContext): Unit = _levelContext = Option(levelContext)

  override def onPause(): Unit = {
    pauseScreen.visible = true
    canvas.opacity = 0.5

    listener.onPauseLevel()
  }

  override def onResume(): Unit = {
    pauseScreen.visible = false
    canvas.opacity = 1

    listener.onResumeLevel()
  }

  override def onExit(): Unit = {
    listener.onStopLevel()
  }

  /**
    * OnMouseClicked handler
    */
  onMouseClicked = mouseEvent => {
    /* Creating a circle representing the player click */
    val clickCircle = Circle(mouseEvent.getX, mouseEvent.getY, 2.0, defaultPlayerColor)
    content.add(clickCircle)
    val fadeOutTransition = new FadeTransition(Duration.apply(2000), clickCircle) {
      fromValue = 1.0
      toValue = 0.0
      onFinished = _ => content.remove(clickCircle)
    }
    fadeOutTransition.play()

    levelContext match {
      case Some(lc) => lc pushEvent MouseEventWrapper(Point(mouseEvent.getX, mouseEvent.getY))
      case _ =>
    }
  }

  override def onLevelSetup(mapShape: MapShape): Unit = mapDrawable match {
    case Some(e) => throw new IllegalStateException("Map has already been set")
    case _ =>
      mapShape match {
        case c: MapShape.Circle => mapDrawable = Option(new StaticImageDrawable(ImageLoader.getImage("/textures/cell.png"),Point(null, null), c.radius, c.radius, canvas.graphicsContext2D))
        case r: MapShape.Rectangle => mapDrawable = Option(new StaticImageDrawable(ImageLoader.getImage("/textures/cell.png"),Point(null, null), r.base, r.height, canvas.graphicsContext2D))
      }

      /* Starting the level */
      startLevel()
  }

  override def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {

    var entitiesWrappers : Seq[(DrawableWrapper, Color)] = Seq()
    var specialWrappers : Seq[(DrawableWrapper, Color)] = entities filter(e => e.entityType.equals(EntityType.Attractive) || e.entityType.equals(EntityType.Repulse)) map(e => e.entityType match {
      case EntityType.Attractive => (e, attractiveCellColor)
      case EntityType.Repulse => (e, repulsiveCellColor)
    })

    playerEntity match {
      /* The player is present */
      case Some(pe) => entitiesWrappers = calculateColors(defaultEntityMinColor, defaultEntityMaxColor, defaultPlayerColor, pe, entities)
      /* The player is not present */
      case _ => entitiesWrappers = calculateColors(defaultEntityMinColor, defaultEntityMaxColor, entities)
    }

    /* We must draw to the screen the entire collection */
    Platform.runLater({
      /* Clear the screen */
      canvas.graphicsContext2D.clearRect(0, 0, width.value, height.value)
      /* Draw the background */
      canvas.graphicsContext2D.drawImage(backgroundDrawable.image, 0, 0, width.value, height.value)
      /* Draw the entities */
      (entitiesWrappers ++ specialWrappers)foreach(e => cellDrawable.draw(e._1, e._2))
      /* Draw the map */
      mapDrawable match {
        case Some(map) => map.draw()
        case _ =>
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
        val endRadius = getEntitiesExtremeRadiusValues(entities)

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
            (e, playerColor.interpolate(maxColor, normalizedRadius))
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
    val sorted = entities.sortWith(_.radius < _.radius)
    /* Retrieving the min and the max radius values */
    sorted match {
      case head +: _ :+ tail => (head.radius, tail.radius)
      case head +: _ => (head.radius, head.radius)
      case _ => throw new IllegalArgumentException("Could not determine the min and max radius from an empty sequence of entities")
    }
  }

}

/**
  * Trait which gets notified when a LevelScene event occurs
  */
trait LevelSceneListener {

  /**
    * Called when the level gets paused
    */
  def onPauseLevel()

  /**
    * Called when the level gets resumed
    */
  def onResumeLevel()

  /**
    * Called when the level gets stopped
    */
  def onStopLevel()

}
