package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.mvc.controller.manager.sounds.{MusicPlayer, SoundsType}
import it.unibo.osmos.redux.mvc.controller.LevelInfo
import it.unibo.osmos.redux.mvc.model.MapShape
import it.unibo.osmos.redux.mvc.view.ViewConstants
import it.unibo.osmos.redux.mvc.view.ViewConstants.Window._
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Colors._
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures._
import it.unibo.osmos.redux.mvc.view.components.level.{LevelScreen, LevelStateBoxListener}
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, LevelContextListener}
import it.unibo.osmos.redux.mvc.view.drawables._
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import it.unibo.osmos.redux.utils.MathUtils._
import it.unibo.osmos.redux.utils.Point
import javafx.scene.input.{KeyCode, MouseEvent}
import scalafx.animation.FadeTransition
import scalafx.application.Platform
import scalafx.beans.property.BooleanProperty
import scalafx.geometry.Pos
import scalafx.scene.canvas.Canvas
import scalafx.scene.effect.Light.Spot
import scalafx.scene.effect.{DropShadow, Lighting}
import scalafx.scene.image.Image
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle, Shape}
import scalafx.scene.text.{Font, Text}
import scalafx.stage.Stage
import scalafx.util.Duration

/**
  * This scene holds and manages a single level
  *
  * @param parentStage        the parent stage
  * @param levelInfo          the level info
  * @param listener           the listener
  * @param upperSceneListener the upper scene listener to manage the previously scene events
  */
class LevelScene(override val parentStage: Stage, val levelInfo: LevelInfo, val listener: LevelSceneListener,
                 val upperSceneListener: UpperLevelSceneListener)
  extends BaseScene(parentStage) with LevelContextListener with LevelStateBoxListener {

  MusicPlayer.play(SoundsType.level)

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

    val light: Spot = new Spot()
    light.color = Color.White
    light.x <== width / 2
    light.y <== height / 2
    light.z = 210
    light.pointsAtX <== width / 2
    light.pointsAtY <== height / 2
    light.pointsAtZ = -10
    light.specularExponent = 2.0

    val lighting: Lighting = new Lighting()
    lighting.light = light
    lighting.surfaceScale = 1.0
    lighting.diffuseConstant = 2.0

    pickOnBounds = false

    effect = lighting
  }

  /**
    * The screen showed when the game is paused (with a bound property)
    */
  private val pauseScreen = LevelScreen.Builder(this)
    .withText("Game paused", 30, Color.White)
    .withButton("Resume", _ => onResume())
    .withButton("Return to Level Selection", _ => onExit())
    .build()
  pauseScreen.visible <== paused

  /**
    * The splash screen showed when the game is paused
    */
  private val splashScreen = LevelScreen.Builder(this)
    .withText(if (levelInfo != null) levelInfo.victoryRule.toString.replace("_", " ") else "", 50, Color.White)
    .build()
  splashScreen.opacity = 0.0

  /* We start the level */
  private def startLevel(): Unit = {
    /* Splash screen animation, starting with a FadeIn */
    new FadeTransition(Duration.apply(2000), splashScreen) {
      fromValue = 0.0
      toValue = 1.0
      /* FadeOut */
      onFinished = _ => new FadeTransition(Duration.apply(2000), splashScreen) {
        fromValue = 1.0
        toValue = 0.0
        /* Showing the canvas */
        onFinished = _ => {
          canvas.opacity = 1.0
          content.remove(splashScreen)
          /* Adding the mapBorder */
          content.add(mapBorder.get)
          /* Removing the splash screen to reduce the load. Then the level is started */
          listener.onStartLevel()
        }
      }.play()
    }.play()
  }

  /**
    * The splash screen text displayed when speeding up or slowing down the game
    */
  private val speedScreenText = new Text("") {
    font = Font.font("Verdana", 30)
    fill = Color.White
  }

  private val speedChangeScreen: VBox = new VBox() {
    alignment = Pos.TopRight
    alignmentInParent = Pos.TopRight
    children = speedScreenText
  }
  speedChangeScreen.opacity = 0.0

  /**
    * The images used to draw cells, background and level
    */
  private val cellDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(cellTexture), canvas.graphicsContext2D)
  private val playerCellDrawable: CellDrawable = new CellWithSpeedDrawable(ImageLoader.getImage(playerCellTexture), canvas.graphicsContext2D)
  private val attractiveDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(attractiveTexture), canvas.graphicsContext2D)
  private val repulsiveDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(repulsiveTexture), canvas.graphicsContext2D)
  private val antiMatterDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(antiMatterTexture), canvas.graphicsContext2D)
  private val sentientDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(sentientTexture), canvas.graphicsContext2D)
  private val controlledDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(controllerTexture), canvas.graphicsContext2D)

  private val backgroundImage: Image = ImageLoader.getImage(backgroundTexture)
  private var mapBorder: Option[Shape] = Option.empty

  /**
    * The content of the whole scene
    */
  content = new StackPane() {
    children = Seq(canvas, speedChangeScreen, splashScreen, pauseScreen)
  }

  /**
    * The level context, created with the LevelScene. It still needs to be properly setup
    */
  protected var _levelContext: Option[LevelContext] = Option.empty

  def levelContext: Option[LevelContext] = _levelContext

  def levelContext_=(levelContext: LevelContext): Unit = _levelContext = Option(levelContext)

  def onPause(): Unit = {
    canvas.opacity = 0.3
    paused.value = true

    listener.onPauseLevel()
  }

  override def onResume(): Unit = {
    canvas.opacity = 1
    paused.value = false

    listener.onResumeLevel()
  }

  override def onExit(): Unit = {
    goToPreviousScene()
    listener.onStopLevel()
  }

  /**
    * Called when the user has to go to the LevelSelectionScene
    */
  private def goToPreviousScene(): Unit = {
    MusicPlayer.play(SoundsType.menu)
    upperSceneListener.onStopLevel()
  }

  /**
    * OnMouseClicked handler, reacting only if the game is not paused
    */
  onMouseClicked = mouseEvent => if (!paused.value) {
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
    * OnKeyPressed handler, reacting to esc, and arrow key press:
    *  - the esc key pauses the game,
    *  - the up and right keys speed up the game
    *  - the down and left keys slow down the game
    */
  onKeyPressed = keyEvent => keyEvent.getCode match {
    case KeyCode.ESCAPE =>
      if (paused.value) {
        onResume()
      } else {
        onPause()
      }
    case KeyCode.UP | KeyCode.RIGHT =>
      listener.onLevelSpeedChanged(true)
      displaySpeedChange("Speed up ►►")
    case KeyCode.DOWN | KeyCode.LEFT =>
      listener.onLevelSpeedChanged()
      displaySpeedChange("Speed down ◄◄")
    case _ => //do nothing
  }

  /*VBox(spacing = 4) {
    prefWidth <== parentScene.width
    prefHeight <== parentScene.height
    alignment = Pos.Center
    alignmentInParent = Pos.Center
    parentScene fill = Color.Black

    children = components*/
  private def displaySpeedChange(text: String): Unit = {
    speedScreenText.text = text
    /* Splash screen animation, starting with a FadeIn */
    new FadeTransition(Duration.apply(300), speedChangeScreen) {
      fromValue = 0.0
      toValue = 1.0
      autoReverse = true
      /* FadeOut */
      onFinished = _ => new FadeTransition(Duration.apply(300), speedChangeScreen) {
        fromValue = 1.0
        toValue = 0.0
        autoReverse = true

      }.play()
    }.play()
  }

  /**
    * Sends a MouseEventWrapper to the LevelContextListener
    *
    * @param mouseEvent the mouse event
    */
  protected def sendMouseEvent(mouseEvent: MouseEvent): Unit = levelContext match {
    case Some(lc) => if (!paused.value) lc notifyMouseEvent MouseEventWrapper(Point(mouseEvent.getX - halfWindowWidth, mouseEvent.getY - halfWindowHeight), lc.getPlayerUUID)
    case _ =>
  }

  override def onLevelSetup(mapShape: MapShape): Unit = mapBorder match {
    case Some(_) => throw new IllegalStateException("Map has already been set")
    case _ =>
      val center = Point(mapShape.center.x + halfWindowWidth, mapShape.center.y + halfWindowHeight)
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
      mapBorder.get.strokeWidth = 5.0
      mapBorder.get.opacity <== canvas.opacity
      mapBorder.get.pickOnBounds = false
      mapBorder.get.mouseTransparent = true
      mapBorder.get.effect = new DropShadow {
        color = Color.White
        radius = 10.0
      }

      Platform.runLater({


        /* Starting the level */
        startLevel()
      })
  }

  override def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {

    var entitiesWrappers: Seq[(DrawableWrapper, Color)] = Seq()

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
      playerEntity match {
        case Some(pe) => entitiesWrappers foreach (e => e._1 match {
          case `pe` => playerCellDrawable.draw(e._1, e._2)
          case _ => drawEntity(e._1, e._2)
        })
        case _ => entitiesWrappers foreach (e => drawEntity(e._1, e._2))
      }
    })
  }

  /**
    * Used to draw the correct entity according to its type
    *
    * @param drawableWrapper the drawableWrapper
    * @param color           the border color
    */
  private def drawEntity(drawableWrapper: DrawableWrapper, color: Color): Unit = {
    drawableWrapper.entityType match {
      case EntityType.Attractive => attractiveDrawable.draw(drawableWrapper, color)
      case EntityType.Repulsive => repulsiveDrawable.draw(drawableWrapper, color)
      case EntityType.AntiMatter => antiMatterDrawable.draw(drawableWrapper, color)
      case EntityType.Sentient => sentientDrawable.draw(drawableWrapper, color)
      case EntityType.Controlled => controlledDrawable.draw(drawableWrapper, color)
      case _ => cellDrawable.draw(drawableWrapper, color)
    }
  }

  override def onLevelEnd(levelResult: Boolean): Unit = {
    /* Calling stop level */
    listener.onStopLevel(levelResult)

    /* Creating an end screen with a button */
    val endScreen = LevelScreen.Builder(this)
      .withText(if (levelResult) "You won!" else "You lost.", 50, Color.White)
      .withButton("Return to Level Selection", _ => goToPreviousScene())
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
        /* Calculate the min and max radius among the entities */
        val endRadius = getEntitiesExtremeRadiusValues(entities)

        entities map (e => {
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
    * @param entities     the input entities
    * @param playerEntity the player entity
    * @param minColor     the base lower Color
    * @param maxColor     the base upper Color
    * @param playerColor  the player Color
    * @return the sequence of pair where the first field is the entity and the second is the color
    */
  private def calculateColors(entities: Seq[DrawableWrapper], playerEntity: DrawableWrapper,
                              minColor: Color = ViewConstants.Entities.Colors.defaultEntityMinColor, maxColor: Color = ViewConstants.Entities.Colors.defaultEntityMaxColor,
                              playerColor: Color = Color.Green): Seq[(DrawableWrapper, Color)] = {
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
    *
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
  def onStopLevel(victory: Boolean = false)

  /**
    * Called when the level speed changes
    *
    * @param increment If the speed needs to increased or decreased
    */
  def onLevelSpeedChanged(increment: Boolean = false)

}
