package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.components.EntityType
import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities._
import it.unibo.osmos.redux.mvc.view.components.level.{LevelScreen, LevelStateBox, LevelStateBoxListener}
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, LevelContextListener}
import it.unibo.osmos.redux.mvc.view.drawables._
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import it.unibo.osmos.redux.utils.MathUtils._
import it.unibo.osmos.redux.utils.Point
import scalafx.animation.FadeTransition
import scalafx.application.Platform
import scalafx.beans.property.BooleanProperty
import scalafx.scene.canvas.Canvas
import scalafx.scene.image.Image
import javafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle, Shape}
import scalafx.stage.Stage
import scalafx.util.Duration

/**
  * This scene holds and manages a single level
  */
class LevelScene(override val parentStage: Stage, val listener: LevelSceneListener, val upperSceneListener: UpperLevelSceneListener) extends BaseScene(parentStage)
  with LevelContextListener with LevelStateBoxListener {

  private val TEXTURE_FOLDER = "/textures/"

  /**
    * The current game pending state: true if the game is paused
    */
  private var paused: BooleanProperty = BooleanProperty(false)

  /**
    * The canvas which will draw the elements on the screen
    */
  private val canvas: Canvas = new Canvas(parentStage.getWidth, parentStage.getHeight) {
    width <== parentStage.width
    height <== parentStage.height
    cache = true
    opacity = 0.0
  }

  /**
    * The screen showed when the game is paused (with a bound property)
    */
  private val pauseScreen = LevelScreen.Builder(this)
    .withText("Game paused", 30, Color.White)
    .build()
  pauseScreen.visible <== paused

  /**
    * The splash screen showed when the game is paused
    */
  private val splashScreen = LevelScreen.Builder(this)
    .withText("Become huge", 50, Color.White)
    .build()

  /**
    * The upper state box
    */
  protected val levelStateBox = new LevelStateBox(this,4.0)

  /* We start the level */
  private def startLevel(): Unit = {
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
          /* Removing the splash screen to reduce the load. Then the level is starte */
          onFinished = _ => content.remove(splashScreen); listener.onStartLevel()
        }.play()
      }.play()
    }.play()
  }

  /**
    * The images used to draw cells, background and level
    */
  private val cellDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(TEXTURE_FOLDER + "cell_blue.png"), canvas.graphicsContext2D)
  private val playerCellDrawable: CellDrawable = new CellWithSpeedDrawable(ImageLoader.getImage(TEXTURE_FOLDER + "cell_green.png"), canvas.graphicsContext2D)
  private val attractiveDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(TEXTURE_FOLDER + "cell_red.png"), canvas.graphicsContext2D)
  private val repulsiveDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(TEXTURE_FOLDER + "cell_yellow.png"), canvas.graphicsContext2D)
  private val antiMatterDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(TEXTURE_FOLDER + "cell_dark_blue.png"), canvas.graphicsContext2D)
  private val sentientDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(TEXTURE_FOLDER + "cell_purple.png"), canvas.graphicsContext2D)
  private val opponentDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(TEXTURE_FOLDER + "cell_violet.png"), canvas.graphicsContext2D)

  private val backgroundImage: Image = ImageLoader.getImage(TEXTURE_FOLDER + "background.png")
  private var mapBorder: Option[Shape] = Option.empty

  /**
    * The content of the whole scene
    */
  content = Seq(canvas, pauseScreen, levelStateBox, splashScreen)

  /**
    * The level context, created with the LevelScene. It still needs to be properly setup
    */
  protected var _levelContext: Option[LevelContext] = Option.empty
  def levelContext: Option[LevelContext] = _levelContext
  def levelContext_= (levelContext: LevelContext): Unit = _levelContext = Option(levelContext)

  override def onPause(): Unit = {
    paused.value = true
    canvas.opacity = 0.5

    listener.onPauseLevel()
  }

  override def onResume(): Unit = {
    paused.value = false
    canvas.opacity = 1

    listener.onResumeLevel()
  }

  override def onExit(): Unit = {
    upperSceneListener.onStopLevel()
    listener.onStopLevel()
  }

  /**
    * OnMouseClicked handler, reacting only if the game is not paused
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

    /* Sending the event */
    sendMouseEvent(mouseEvent)
  }

  /**
    * Sends a MouseEventWrapper to the LevelContextListener
    * @param mouseEvent the mouse event
    */
  protected def sendMouseEvent(mouseEvent: MouseEvent): Unit  = levelContext match {
    case Some(lc) => if (!paused.value) lc notifyMouseEvent MouseEventWrapper(Point(mouseEvent.getX, mouseEvent.getY))
    case _ =>
  }

  override def onLevelSetup(mapShape: MapShape): Unit = mapBorder match {
    case Some(_) => throw new IllegalStateException("Map has already been set")
    case _ =>
      val center = Point(mapShape.center._1, mapShape.center._2)
      mapShape match {
        case c: MapShape.Circle => mapBorder = Option(new Circle {
          centerX = center.x
          centerY = center.y
          radius = c.radius
        })
        case r: MapShape.Rectangle => mapBorder = Option(new Rectangle {
          x = center.x - r.base / 2
          y = center.y - r.height / 2
          width = r.base
          height = r.height
        })
      }

      /* Configuring the mapBorder */
      mapBorder.get.fill = Color.Transparent
      mapBorder.get.stroke = Color.White
      mapBorder.get.strokeWidth = 2.0
      mapBorder.get.opacity <== canvas.opacity

      /* Adding the mapBorder */
      content.add(mapBorder.get)

      /* Starting the level */
      startLevel()
  }

  override def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {

    var entitiesWrappers : Seq[(DrawableWrapper, Color)] = Seq()

    playerEntity match {
      /* The player is present */
      case Some(pe) => entitiesWrappers = calculateColors(entities, pe)
      /* The player is not present */
      case _ => entitiesWrappers = calculateColorsWithoutPlayer(entities)
    }

    /* We must draw to the screen the entire collection */
    Platform.runLater({
      /* Clear the screen */
      canvas.graphicsContext2D.clearRect(0, 0, width.value, height.value)
      /* Draw the background */
      canvas.graphicsContext2D.drawImage(backgroundImage, 0, 0, width.value, height.value)
      /* Draw the entities */
      playerEntity match  {
        case Some(pe) => entitiesWrappers foreach(e => e._1 match {
          case `pe` => playerCellDrawable.draw(e._1, e._2)
          case _ => drawEntity(e._1, e._2)
        })
        case _ => entitiesWrappers foreach(e => drawEntity(e._1, e._2))
      }
    })
  }

  /**
    * Used to draw the correct entity according to its type
    * @param drawableWrapper the drawableWrapper
    * @param color the border color
    */
  private def drawEntity(drawableWrapper: DrawableWrapper, color: Color): Unit = {
    drawableWrapper.entityType match {
      case EntityType.Attractive => attractiveDrawable.draw(drawableWrapper, color)
      case EntityType.Repulse => repulsiveDrawable.draw(drawableWrapper, color)
      case EntityType.AntiMatter => antiMatterDrawable.draw(drawableWrapper, color)
      case EntityType.Sentient => sentientDrawable.draw(drawableWrapper, color)
      case EntityType.Controlled => opponentDrawable.draw(drawableWrapper, color)
      case _ => cellDrawable.draw(drawableWrapper, color)
    }
  }

  override def onLevelEnd(levelResult: Boolean): Unit = {
    /* Creating an end screen with a button */
    val endScreen = LevelScreen.Builder(this)
      .withText(if (levelResult) "You won!" else "You lost.", 50, Color.White)
      .withButton("Return to Level Selection", _ => onExit())
      .build()
    endScreen.opacity = 0.0

    /* Fade in/fade out transition */
    new FadeTransition(Duration.apply(3000), canvas) {
      fromValue = 1.0
      toValue = 0.0
      onFinished = _ => {
        /* Remove all the contents and add the end screen */
        content.clear()
        content.add(endScreen)
        new FadeTransition(Duration.apply(3000), endScreen) {
          fromValue = 0.0
          toValue = 1.0
        }.play()
      }
    }.play()
  }

  /**
    * This method calculates the color of the input entities, interpolating and normalizing it according to the entities size
    * @param minColor the base lower Color
    * @param maxColor the base upper Color
    * @param entities the input entities
    * @return the sequence of pair where the first field is the entity and the second is the color
    */
  private def calculateColorsWithoutPlayer(entities: Seq[DrawableWrapper], minColor: Color = Color.LightBlue, maxColor: Color = Color.DarkRed): Seq[(DrawableWrapper, Color)] = {
    entities match {
      case Nil => Seq()
      case _ =>
      /* Calculate the min and max radius among the entities */
      val endRadius = getEntitiesExtremeRadiusValues(entities)

      entities map( e => {
        // Normalize the entity radius
        val normalizedRadius = normalize(e.radius, endRadius._1, endRadius._2)
        /* Create a pair where the second value is the interpolated color between the two base colors */
        (e, minColor.interpolate(maxColor, normalizedRadius))
      }) seq
    }
  }

  /**
    * This method calculates the color of the input entities when the player is present
    *
    * @param entities the input entities
    * @param playerEntity the player entity
    * @param minColor the base lower Color
    * @param maxColor the base upper Color
    * @param playerColor the player Color
    * @return the sequence of pair where the first field is the entity and the second is the color
    */
  private def calculateColors(entities: Seq[DrawableWrapper], playerEntity: DrawableWrapper, minColor: Color = Color.LightBlue, maxColor: Color = Color.DarkRed, playerColor: Color = Color.Green): Seq[(DrawableWrapper, Color)] = {
    entities match {
      case Nil => Seq()
      case _ =>
        /* Calculate the min and max radius among the entities, considering the player */
        entities map {
          case e if e.radius == playerEntity.radius => (e, playerColor)
          /* The entity is smaller than the player so it's color hue will approach the min one */
          case e if e.radius < playerEntity.radius => (e, minColor)
          /* The entity is larger than the player so it's color hue will approach the max one */
          case e => (e, maxColor)
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
  * Trait used by LevelScene to notify an event to the upper scene
  */
trait UpperLevelSceneListener {

  /**
    * Called when the level gets stopped
    */
  def onStopLevel()

}

/**
  * Trait which gets notified when a LevelScene event occurs
  */
trait LevelSceneListener {

  /**
    * Called when the level gets started
    */
  def onStartLevel()

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
