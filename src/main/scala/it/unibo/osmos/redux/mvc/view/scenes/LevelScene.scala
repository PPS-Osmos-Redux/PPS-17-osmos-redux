package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.mvc.controller.levels.structure.{LevelInfo, MapShape}
import it.unibo.osmos.redux.mvc.controller.manager.sounds.{MusicPlayer, SoundsType}
import it.unibo.osmos.redux.mvc.view.ViewConstants
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Colors._
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures._
import it.unibo.osmos.redux.mvc.view.ViewConstants.Window._
import it.unibo.osmos.redux.mvc.view.components.level.{LevelScreen, LevelStateBoxListener}
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, LevelContextListener}
import it.unibo.osmos.redux.mvc.view.drawables._
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import it.unibo.osmos.redux.utils.MathUtils._
import it.unibo.osmos.redux.utils.{MathUtils, Point}
import javafx.scene.input.{KeyCode, MouseEvent}
import scalafx.animation.FadeTransition
import scalafx.application.Platform
import scalafx.beans.property.{BooleanProperty, DoubleProperty}
import scalafx.geometry.Pos
import scalafx.scene.canvas.Canvas
import scalafx.scene.effect.Light.Spot
import scalafx.scene.effect.{DropShadow, Lighting}
import scalafx.scene.image.{Image, ImageView}
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
//noinspection ForwardReference
class LevelScene(override val parentStage: Stage, val levelInfo: LevelInfo, val listener: LevelSceneListener, val upperSceneListener: BackClickListener)
  extends DefaultBackScene(parentStage, upperSceneListener) with LevelContextListener {

  /** Level images */
  private object LevelDrawables {
    val cellDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(cellTexture), canvas.graphicsContext2D)
    val playerCellDrawable: CellDrawable = new CellWithSpeedDrawable(ImageLoader.getImage(playerCellTexture), canvas.graphicsContext2D)
    val attractiveDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(attractiveTexture), canvas.graphicsContext2D)
    val repulsiveDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(repulsiveTexture), canvas.graphicsContext2D)
    val antiMatterDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(antiMatterTexture), canvas.graphicsContext2D)
    val sentientDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(sentientTexture), canvas.graphicsContext2D)
    val controlledDrawable: CellDrawable = new CellDrawable(ImageLoader.getImage(controllerTexture), canvas.graphicsContext2D)
    val backgroundImage: Image = ImageLoader.getImage(backgroundTexture)
  }

  /** Level state variables */
  private object LevelState {
    /** The current game pending state: true if the game is paused */
    var paused: BooleanProperty = BooleanProperty(false)
    /** Value indicating when the user can execute actions like pausing the game or moving around */
    var inputEnabled = false
    /** Scrolling delta */
    val delta = 1.1
    /** Max zoom out scale */
    val maxZoomOutScale = 1.0
    /** Max zoom in scale */
    val maxZoomInScale = 2.0
  }

  /** Level player variables */
  private object LevelPlayer {
    /** Player position x-coordinate */
    val playerPosX : DoubleProperty = DoubleProperty(0.0)
    /** Player position y-coordinate */
    val playerPosY : DoubleProperty = DoubleProperty(0.0)
  }

  /** The level context, created with the LevelScene */
  protected var _levelContext: Option[LevelContext] = Option.empty
  def levelContext: Option[LevelContext] = _levelContext
  def levelContext_=(levelContext: LevelContext): Unit = _levelContext = Option(levelContext)

  /** The level border */
  private var mapBorder: Shape = _

  /** Playing level music */
  MusicPlayer.play(SoundsType.level)

  /** DefaultBackScene goBack button configurations */
  setText("Return to Level Selection")
  setAdditionalAction(() => onExit())

  /** The canvas which will draw the elements on the screen */
  private val canvas: Canvas = new Canvas {
    width <== parentStage.width
    height <== parentStage.height
    cache = true
    opacity = 0.0

    val light: Spot = new Spot()
    light.color = Color.White
    light.x <== (LevelPlayer.playerPosX + halfWindowWidth) * this.scaleX
    light.y <== (LevelPlayer.playerPosY + halfWindowHeight) * this.scaleY
    light.z = 200
    light.pointsAtX <== (LevelPlayer.playerPosX + halfWindowWidth) * this.scaleX
    light.pointsAtY <== (LevelPlayer.playerPosY + halfWindowHeight) * this.scaleY
    light.pointsAtZ = -10
    light.specularExponent = 2.0

    val lighting: Lighting = new Lighting()
    lighting.light = light
    lighting.surfaceScale = 1.0
    lighting.diffuseConstant = 2.0

    pickOnBounds = false

    effect = lighting
  }

  /** The splash screen showed when the game is paused */
  private val splashScreen = LevelScreen.Builder(this)
    .withText(if (levelInfo != null) levelInfo.victoryRule.toString.replace("_", " ") else "", 50, Color.White)
    .build()
  splashScreen.opacity = 0.0

  /** The screen showed when the game is paused (with a bound property) */
  private val pauseScreen = LevelScreen.Builder(this)
    .withText("Game paused", 30, Color.White)
    .withButton("Resume", _ => onResume())
    .withNode(goBack)
    .build()
  pauseScreen.visible <== LevelState.paused

  /** The speed screen text displayed when speeding up or slowing down the game */
  private var speedScreenText: Text = new Text("") {
    font = Font.font("Verdana", 30)
    fill = Color.White
  }

  /** The speed screen displayed when speeding up or slowing down the game */
  private val speedChangeScreen: VBox = new VBox() {
    alignment = Pos.TopRight
    alignmentInParent = Pos.TopRight
    opacity = 0.0
    children = speedScreenText
  }

  /** The content of the whole scene, using a stack pane as the main container */
  content = new StackPane() {
    children = Seq(canvas, speedChangeScreen, splashScreen, pauseScreen)
  }

  /** Method to start the level */
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
          content.add(mapBorder)
          LevelState.inputEnabled = true
          /* Removing the splash screen to reduce the load. Then the level is started */
          listener.onStartLevel()
        }
      }.play()
    }.play()
  }

  def onPause(): Unit = {
    canvas.opacity = 0.3
    LevelState.paused.value = true

    listener.onPauseLevel()
  }

  protected def onResume(): Unit = {
    canvas.opacity = 1
    LevelState.paused.value = false

    listener.onResumeLevel()
  }

  private def onExit(): Unit = {
    MusicPlayer.play(SoundsType.menu)
    listener.onStopLevel()
  }

  /** Called when the user has to go to the LevelSelectionScene */
  private def goToPreviousScene(): Unit = {
    MusicPlayer.play(SoundsType.menu)
    upperSceneListener.onBackClick()
  }

  /** OnMouseClicked handler, reacting only if the game is not paused */
  onMouseClicked = mouseEvent => if (!LevelState.paused.value && LevelState.inputEnabled) {
    /** Creating a circle representing the player click */
    val clickCircle = Circle(mouseEvent.getX, mouseEvent.getY, 2.0, defaultPlayerColor)
    content.add(clickCircle)
    val fadeOutTransition = new FadeTransition(Duration.apply(2000), clickCircle) {
      fromValue = 1.0
      toValue = 0.0
      onFinished = _ => content.remove(clickCircle)
    }
    fadeOutTransition.play()

    /** Sending the event */
    sendMouseEvent(mouseEvent)
  }

  /** OnScroll handler, managing the scale */
  onScroll = scrollEvent => if (!LevelState.paused.value && LevelState.inputEnabled) {

    /** Retrieving the current scale */
    var scale = canvas.scaleY.value
    if (scrollEvent.getDeltaY < 0) {
      scale /= LevelState.delta
    } else {
      scale *= LevelState.delta
    }

    /** Limiting the scale */
    scale = MathUtils.clamp(scale, LevelState.maxZoomOutScale, LevelState.maxZoomInScale)

    /** Updating the canvas scale */
    canvas.scaleX = scale
    canvas.scaleY = scale

    scrollEvent.consume()
  }

  /**
    * Method which limits a value between a minimum and a maximum ones
    * @param value the value
    * @param min the minimum value
    * @param max the maximum value
    * @return the clamped value
    */
  /*private def clamp(value: Double, min: Double, max: Double): Double = {
    value match {
      case v if v < min => min
      case v if v > max => max
      case _ => value
    }
  }*/

  /**
    * This method updates the camera position
    * @param x the translation x-coordinate
    * @param y the translation y-coordinate
    */
  private def setCameraPivot(x: Double, y: Double): Unit = {
    canvas.translateX = x
    canvas.translateY = y
  }

  /**
    * OnKeyPressed handler, reacting to esc, and arrow key press:
    *  - the esc key pauses the game,
    *  - the up and right keys speed up the game
    *  - the down and left keys slow down the game
    */
  onKeyPressed = keyEvent => if(LevelState.inputEnabled) keyEvent.getCode match {
    case KeyCode.ESCAPE =>
      if (LevelState.paused.value) {
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

  /**
    * This method shows the new speed value, called when the user changes it
    * @param text the new speed screen text
    */
  private def displaySpeedChange(text: String): Unit = {
    /** Changing the speed screen text */
    speedScreenText.text = text
    /** Splash screen animation, starting with a FadeIn */
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
    case Some(lc) =>
      if (!LevelState.paused.value && LevelState.inputEnabled) {
        /** Transforming click coordinates considering window size and zoom */
        val x = mouseEvent.getX - halfWindowWidth - canvas.getTranslateX / canvas.getScaleX
        val y = mouseEvent.getY - halfWindowHeight - canvas.getTranslateY / canvas.getScaleY
        lc notifyMouseEvent MouseEventWrapper(Point(x, y), lc.getPlayerUUID)
      }
    case _ =>
  }

  override def onLevelSetup(mapShape: MapShape): Unit = mapShape match {
    case null => throw new IllegalStateException("Map set was null")
    case _ =>
      val center = Point(mapShape.center.x + halfWindowWidth, mapShape.center.y + halfWindowHeight)
      mapShape match {
        case c: MapShape.Circle => mapBorder = new Circle {
          centerX = center.x
          centerY = center.y
          radius = c.radius
        }
        case r: MapShape.Rectangle => mapBorder = new Rectangle {
          x = center.x - r.base / 2
          y = center.y - r.height / 2
          width = r.base
          height = r.height
        }
      }

      /** Configuring the mapBorder */
      // TODO: make it colored white if bouncing, red if instant death
      mapBorder.fill = Color.Transparent
      mapBorder.stroke = Color.White
      mapBorder.strokeWidth = 5.0
      mapBorder.opacity <== canvas.opacity
      mapBorder.pickOnBounds = false
      mapBorder.mouseTransparent = true
      mapBorder.effect = new DropShadow() {
        color = Color.White
        radius = 10.0
      }

      /** Scale binding (with canvas)*/
      mapBorder.scaleX <== canvas.scaleX
      mapBorder.scaleY <== canvas.scaleY
      /** Translate binding (with canvas)*/
      mapBorder.translateX <== (- canvas.scaleX * LevelPlayer.playerPosX)
      mapBorder.translateY <== (- canvas.scaleY * LevelPlayer.playerPosY)

      Platform.runLater({
        /** Starting the level */
        startLevel()
      })
  }

  override def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {

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
      canvas.graphicsContext2D.clearRect(0, 0, width.value, height.value)
      canvas.graphicsContext2D.drawImage(LevelDrawables.backgroundImage, 0, 0, width.value, height.value)

      /** Draw the entities */
      playerEntity match {
        case Some(pe) => entitiesWrappers foreach (e => e._1 match {
          case `pe` =>
            if (canvas.getScaleY == 1.0) {
              LevelPlayer.playerPosX.value = 0.0
              LevelPlayer.playerPosY.value = 0.0
              setCameraPivot(0, 0)
            } else {
              LevelPlayer.playerPosX.value = e._1.center.x
              LevelPlayer.playerPosY.value = e._1.center.y
              setCameraPivot(- LevelPlayer.playerPosX.value * canvas.getScaleX, - LevelPlayer.playerPosY.value * canvas.getScaleY)
            }
            LevelDrawables.playerCellDrawable.draw(e._1, e._2)
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
      case EntityType.Attractive => LevelDrawables.attractiveDrawable.draw(drawableWrapper, color)
      case EntityType.Repulsive => LevelDrawables.repulsiveDrawable.draw(drawableWrapper, color)
      case EntityType.AntiMatter => LevelDrawables.antiMatterDrawable.draw(drawableWrapper, color)
      case EntityType.Sentient => LevelDrawables.sentientDrawable.draw(drawableWrapper, color)
      case EntityType.Controlled => LevelDrawables.controlledDrawable.draw(drawableWrapper, color)
      case _ => LevelDrawables.cellDrawable.draw(drawableWrapper, color)
    }
  }

  override def onLevelEnd(levelResult: Boolean): Unit = {
    /** Calling stop level */
    listener.onStopLevel(levelResult)

    LevelState.inputEnabled = false

    setAdditionalAction(() => goToPreviousScene())

    /** Creating an end screen with a button */
    val endScreen = LevelScreen.Builder(this)
      .withText(if (levelResult) "You won!" else "You lost.", 50, Color.White)
      .withButton("Return to Level Selection", _ => goToPreviousScene())
      .build()
    endScreen.opacity = 0.0

    /** Fade in/fade out transition */
    new FadeTransition(Duration.apply(3000), canvas) {
      fromValue = 1.0
      toValue = 0.0
      onFinished = _ => {
        /** Remove all the contents and add the end screen */
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
        /** Calculate the min and max radius among the entities */
        val endRadius = getEntitiesExtremeRadiusValues(entities)

        entities map (e => {
          /** Normalize the entity radius*/
          val normalizedRadius = normalize(e.radius, endRadius._1, endRadius._2)
          /** Create a pair where the second value is the interpolated color between the two base colors */
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

  /**
    * This method returns a pair consisting of the min and the max radius found in the entities sequence
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

/**
  * Trait used by LevelScene to notify an event to the upper scene
  */
trait UpperLevelSceneListener extends BackClickListener {

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
    *
    * @param victory true if we won the level, false otherwise
    */
  def onStopLevel(victory: Boolean = false)

  /**
    * Called when the level speed changes
    *
    * @param increment If the speed needs to increased or decreased
    */
  def onLevelSpeedChanged(increment: Boolean = false)

}
