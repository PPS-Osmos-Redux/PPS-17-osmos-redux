package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.levels.structure.{LevelInfo, MapShape}
import it.unibo.osmos.redux.mvc.controller.manager.sounds.{MusicPlayer, SoundsType}
import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Colors._
import it.unibo.osmos.redux.mvc.view.ViewConstants.Level._
import it.unibo.osmos.redux.mvc.view.ViewConstants.Window._
import it.unibo.osmos.redux.mvc.view.components.level.{LevelEntitiesDrawer, LevelScreen}
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, LevelContextListener}
import it.unibo.osmos.redux.mvc.view.drawables._
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.utils.{MathUtils, Point}
import javafx.scene.input.{KeyCode, MouseEvent}
import scalafx.animation.FadeTransition
import scalafx.application.Platform
import scalafx.beans.property.{BooleanProperty, DoubleProperty}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.canvas.Canvas
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle, Shape}
import scalafx.scene.text.{Font, Text}
import scalafx.stage.Stage
import scalafx.util.Duration

/** This scene holds and manages a single level
  *
  * @param parentStage       the parent stage
  * @param levelInfo         the level info
  * @param listener          the listener
  * @param backClickListener the back click listener listener
  */
//noinspection ForwardReference
class LevelScene(override val parentStage: Stage, val levelInfo: LevelInfo, val listener: LevelSceneListener, val backClickListener: BackClickListener)
  extends DefaultBackScene(parentStage, backClickListener) with LevelContextListener {

  /** Playing level music */
  MusicPlayer.play(SoundsType.level)
  /** DefaultBackScene goBack button configurations */
  setText("Return to Level Selection")
  setAdditionalAction(() => onExit())

  /** Level state variables */
  protected object LevelState {
    /** The current game pending state: true if the game is paused */
    var paused: BooleanProperty = BooleanProperty(false)
    /** Value indicating when the user can execute actions like pausing the game or moving around */
    var inputEnabled = false
  }

  /** Level player variables */
  private object LevelPlayer {
    /** Player position x-coordinate */
    val playerPosX: DoubleProperty = DoubleProperty(0.0)
    /** Player position y-coordinate */
    val playerPosY: DoubleProperty = DoubleProperty(0.0)
  }

  /** The level context, created with the LevelScene */
  protected var _levelContext: Option[LevelContext] = Option.empty

  def levelContext: Option[LevelContext] = _levelContext

  def levelContext_=(levelContext: LevelContext): Unit = _levelContext = Option(levelContext)

  /** The level border */
  private var mapBorder: Shape = _

  /** The canvas which will draw the elements on the screen */
  protected val canvas: Canvas = new Canvas {
    width <== parentStage.width
    height <== parentStage.height
    cache = true
    opacity = 0.0
  }

  /** The level entities drawer, which encapsulates all the method used to draw them*/
  private val levelEntitiesDrawer = new LevelEntitiesDrawer(canvas)

  /** The splash screen shown when the game is paused */
  private val splashScreen = LevelScreen.Builder(this)
    .withText(if (levelInfo != null) levelInfo.victoryRule.toString else "", 50, Color.White)
    .build()
  splashScreen.opacity = 0.0

  /** The screen shown when the game is paused (with a bound property) */
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
    margin = Insets(20, 20, 20, 20)
    opacity = 0.0
    children = speedScreenText
  }

  /** The content of the whole scene, using a stack pane as the main container */
  content = new StackPane() {
    children = Seq(canvas, speedChangeScreen, splashScreen, pauseScreen)
  }

  /** Method to start the level */
  private def startLevel(): Unit = {
    /** Splash screen animation, starting with a FadeIn */
    new FadeTransition(Duration.apply(2000), splashScreen) {
      fromValue = 0.0
      toValue = 1.0
      /** FadeOut */
      onFinished = _ => new FadeTransition(Duration.apply(2000), splashScreen) {
        fromValue = 1.0
        toValue = 0.0
        /** Showing the canvas */
        onFinished = _ => {
          canvas.opacity = 1.0
          content.remove(splashScreen)

          /** Adding the mapBorder */
          content.add(mapBorder)
          LevelState.inputEnabled = true

          /** Removing the splash screen to reduce the load. Then the level is started */
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
    backClickListener.onBackClick()
  }

  /** OnMouseClicked handler, reacting only if the game is not paused */
  onMouseClicked = mouseEvent => if (!LevelState.paused.value && LevelState.inputEnabled) {
    /** Creating a circle representing the player click */
    val clickCircle = Circle(mouseEvent.getX, mouseEvent.getY, 2.0, DefaultPlayerColor)
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
      scale /= ScrollingDelta
    } else {
      scale *= ScrollingDelta
    }

    /** Limiting the scale */
    scale = MathUtils.clamp(scale, MaxZoomOutScale, MaxZoomInScale)

    /** Updating the canvas scale */
    canvas.scaleX = scale
    canvas.scaleY = scale

    scrollEvent.consume()
  }

  /** This method updates the camera position
    *
    * @param x the translation x-coordinate
    * @param y the translation y-coordinate
    */
  private def setCameraPivot(x: Double, y: Double): Unit = {
    canvas.translateX = x
    canvas.translateY = y
  }

  /** OnKeyPressed handler, reacting to esc, and arrow key press:
    *  - the esc key pauses the game,
    *  - the up and right keys speed up the game
    *  - the down and left keys slow down the game
    */
  onKeyPressed = keyEvent => if (LevelState.inputEnabled) keyEvent.getCode match {
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

  /** This method shows the new speed value, called when the user changes it
    *
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
      /** FadeOut */
      onFinished = _ => new FadeTransition(Duration.apply(300), speedChangeScreen) {
        fromValue = 1.0
        toValue = 0.0
        autoReverse = true
      }.play()
    }.play()
  }

  /** Sends a MouseEventWrapper to the LevelContextListener
    *
    * @param mouseEvent the mouse event
    */
  protected def sendMouseEvent(mouseEvent: MouseEvent): Unit = levelContext match {
    case Some(lc) =>
      if (!LevelState.paused.value && LevelState.inputEnabled) {
        /** Transforming click coordinates considering window size and zoom */
        val x = mouseEvent.getX - HalfWindowWidth - canvas.getTranslateX / canvas.getScaleX
        val y = mouseEvent.getY - HalfWindowHeight - canvas.getTranslateY / canvas.getScaleY
        lc notifyMouseEvent MouseEventWrapper(Point(x, y), lc.getPlayerUUID)
      }
    case _ =>
  }

  override def onLevelSetup(mapShape: MapShape): Unit = mapShape match {
    case null => throw new IllegalStateException("Map set was null")
    case _ =>
      val center = Point(mapShape.center.x + HalfWindowWidth, mapShape.center.y + HalfWindowHeight)
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

      /** Scale binding (with canvas) */
      mapBorder.scaleX <== canvas.scaleX
      mapBorder.scaleY <== canvas.scaleY

      /** Translate binding (with canvas) */
      mapBorder.translateX <== (-canvas.scaleX * LevelPlayer.playerPosX)
      mapBorder.translateY <== (-canvas.scaleY * LevelPlayer.playerPosY)

      Platform.runLater({
        /** Starting the level */
        startLevel()
      })
  }

  override def onDrawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper]): Unit = {
    /** Calling the level drawer */
    levelEntitiesDrawer.drawEntities(playerEntity, entities, (centerPoint) => {
      /** Managing the callback, updating the camera position */
      if (canvas.getScaleY == 1.0) {
        LevelPlayer.playerPosX.value = 0.0
        LevelPlayer.playerPosY.value = 0.0
        setCameraPivot(0, 0)
      } else {
        LevelPlayer.playerPosX.value = centerPoint.x
        LevelPlayer.playerPosY.value = centerPoint.y
        setCameraPivot(-LevelPlayer.playerPosX.value * canvas.getScaleX, -LevelPlayer.playerPosY.value * canvas.getScaleY)
      }
    })
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

}

/** Trait which gets notified when a LevelScene event occurs */
trait LevelSceneListener {

  /** Called when the level gets started */
  def onStartLevel()

  /** Called when the level gets paused */
  def onPauseLevel()

  /** Called when the level gets resume */
  def onResumeLevel()

  /** Called when the level gets stopped
    *
    * @param victory true if we won the level, false otherwise
    */
  def onStopLevel(victory: Boolean = false)

  /** Called when the level speed changes
    *
    * @param increment If the speed needs to increased or decreased
    */
  def onLevelSpeedChanged(increment: Boolean = false)

}
