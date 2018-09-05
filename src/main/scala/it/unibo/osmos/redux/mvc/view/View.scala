package it.unibo.osmos.redux.mvc.view

import it.unibo.osmos.redux.ecs.entities.CellEntity
import it.unibo.osmos.redux.mvc.controller.Controller
import it.unibo.osmos.redux.mvc.controller.levels.structure.{CollisionRules, LevelInfo, MapShape, VictoryRules}
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.{LevelContext, LobbyContext}
import it.unibo.osmos.redux.mvc.view.stages.{OsmosReduxPrimaryStage, PrimaryStageListener}
import it.unibo.osmos.redux.utils.GenericResponse
import scalafx.application.{JFXApp, Platform}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Label, TextArea}
import scalafx.scene.layout.VBox

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}

/**
  * View base trait
  */
trait View {


  /**
    * Setter. This method sets the reference to the Controller instance
    *
    * @param controller the Controller instance
    */
  def setController(controller: Controller)

}

object View {

  def apply(app: JFXApp): View = new ViewImpl(app)

  /**
    * View implementation, holding the main stage and the current scene
    *
    * @param app a reference to the JFXApp, necessary to the correct setup of the whole application
    */
  class ViewImpl(private val app: JFXApp) extends View with PrimaryStageListener {

    implicit val ec: ExecutionContextExecutor = ExecutionContext.global

    app.stage = OsmosReduxPrimaryStage(this)
    private var controller: Option[Controller] = Option.empty

    override def setController(controller: Controller): Unit = {
      this.controller = Option(controller)
    }

    /**
      * Utility method that checks if controller is not empty and execute f() if that is the case
      *
      * @param f the function which will be executed if controller is not empty
      */
    private def checkController(f: () => Unit): Unit = controller match {
      case Some(_) => f()
      case _ =>
    }

    override def onLevelContextCreated(levelContext: LevelContext, level: String, isCustom: Boolean = false): Unit = checkController(() => controller.get.initLevel(levelContext, level, isCustom))

    override def getSingleLevels: List[LevelInfo] = controller match {
      case Some(c) => c.getSinglePlayerLevels
      case _ => List()
    }

    override def getCustomLevels: List[LevelInfo] = controller match {
      case Some(c) => c.getCustomLevels
      case _ => List()
    }

    override def getMultiPlayerLevels: List[LevelInfo] = controller match {
      case Some(c) => c.getMultiPlayerLevels
      case _ => List()
    }

    override def onSaveLevel(name: String,
                             map: MapShape, victoryRules: VictoryRules.Value, collisionRules: CollisionRules.Value,
                             entities: Seq[CellEntity],
                             callback: Boolean => Unit): Unit = checkController(() => callback(controller.get.saveLevel(name, map, victoryRules, collisionRules, entities)))

    override def onDeleteLevel(level: String, callback: Boolean => Unit): Unit = checkController(() => callback(controller.get.removeLevel(level)))

    override def onStartLevel(): Unit = checkController(() => controller.get.startLevel())

    override def onPauseLevel(): Unit = checkController(() => controller.get.pauseLevel())

    override def onResumeLevel(): Unit = checkController(() => controller.get.resumeLevel())

    override def onStopLevel(victory: Boolean): Unit = checkController(() => controller.get.stopLevel(victory))

    override def onLevelSpeedChanged(increment: Boolean): Unit = checkController(() => controller.get.changeLevelSpeed(increment))

    override def onDisplayError(exception: Throwable): Unit = {
      // TODO change for a better output
      Platform.runLater {
        val dialogPaneContent = new VBox()

        val label = new Label("Stack Trace:")

        val textArea = new TextArea()
        textArea.setText(exception.getMessage)

        dialogPaneContent.getChildren.addAll(label, textArea)

        val alert = new Alert(AlertType.Error) {
          title = "Error Dialog"
          headerText = None
          graphic = null
        }
        // Set content for Dialog Pane
        alert.getDialogPane.setContent(dialogPaneContent)
        alert.showAndWait()
      }
    }

    /**
      * After checking the controller, we ask to enter the lobby asynchronously and call the callback function after the future result
      *
      * @param user         the user requesting to enter the lobby
      * @param lobbyContext the lobby context, which may be used by the server to configure existing lobby users
      * @param callback     the callback
      */
    override def onLobbyRequest(user: User, levelInfo: Option[LevelInfo], lobbyContext: LobbyContext, callback: (User, Option[LevelInfo], LobbyContext, GenericResponse[Boolean]) => Unit): Unit =
      checkController(() => controller.get.initLobby(user, lobbyContext).future.onComplete {
        case Success(value) => callback(user, levelInfo, lobbyContext, value)
        case Failure(e) => onDisplayError(e)
      })

    override def onStartMultiplayerGameClick(levelInfo: LevelInfo): Unit = checkController(() => controller.get.initMultiPlayerLevel(levelInfo).future.onComplete {
      case Failure(e) => onDisplayError(e)
      case Success(_) => //do nothing
    })

  }

}

