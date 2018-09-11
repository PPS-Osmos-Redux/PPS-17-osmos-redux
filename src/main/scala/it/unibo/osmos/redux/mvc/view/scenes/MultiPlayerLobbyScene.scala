package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.levels.structure.LevelInfo
import it.unibo.osmos.redux.mvc.view.components.custom.StyledButton
import it.unibo.osmos.redux.mvc.view.components.multiplayer.{User, UserWithProperties}
import it.unibo.osmos.redux.mvc.view.context.{LobbyContext, LobbyContextListener, MultiPlayerLevelContext}
import it.unibo.osmos.redux.mvc.view.events.{AbortLobby, LobbyEventWrapper}
import scalafx.application.Platform
import scalafx.beans.property.{BooleanProperty, ObjectProperty}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{Button, TableColumn, TableView}
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.stage.Stage

/** Lobby showing other clients or servers playing in multiplayer
  *
  * @param parentStage           the parent stage
  * @param listener              the MultiPlayerLobbySceneListener
  * @param upperSceneListener    the UpperMultiPlayerLobbySceneListener
  * @param user                   the user who requested to enter the lobby
  */
class MultiPlayerLobbyScene(override val parentStage: Stage, val listener: MultiPlayerLobbySceneListener,
                            val upperSceneListener: UpperMultiPlayerLobbySceneListener, val user: User)
  extends BaseScene(parentStage) with LobbyContextListener {

  /** The lobby context, created with the MultiPlayerLobbyScene. It still needs to be properly setup */
  private var _lobbyContext: Option[LobbyContext] = Option.empty
  def lobbyContext: Option[LobbyContext] = _lobbyContext
  def lobbyContext_=(lobbyContext: LobbyContext): Unit = {
    _lobbyContext = Option(lobbyContext)
    /** Subscribe to lobby context events */
    lobbyContext.setListener(this)
    /** Fill table with existing users */
    userList ++= lobbyContext.users.map(_.getUserWithProperty)
  }

  /** The level info, which may be empty if the user is a client */
  private var _levelInfo: Option[LevelInfo] = Option.empty
  def levelInfo_=(levelInfo: Option[LevelInfo]): Unit = _levelInfo = levelInfo

  /** ObservableBuffer holding the current users */
  private val userList = ObservableBuffer[UserWithProperties]()
  /** BooleanProperty representing the visibility of the start button */
  private val isStartGameDisabled = BooleanProperty(true)

  /** TableView linked with the user list */
  val usersTable: TableView[UserWithProperties] = new TableView[UserWithProperties](userList) {
    styleClass.add("multi-player-lobby-table")
    columnResizePolicy = TableView.ConstrainedResizePolicy
    columns ++= List(
      new TableColumn[UserWithProperties, String]() {
        text = "Username"
        cellValueFactory = {
          _.value.username
        }
      }, new TableColumn[UserWithProperties, String]() {
        text = "IP"
        cellValueFactory = {
          _.value.ip
        }
      }, new TableColumn[UserWithProperties, Int]() {
        text = "Port"
        cellValueFactory = p => {
          new ObjectProperty[Int](this, "Port", p.value.port.value)
        }
      }
    )
  }

  private val container: VBox = new VBox(5.0) {
    maxWidth <== parentStage.width / 2
    alignment = Pos.Center
    children = Seq(usersTable)
  }

  /** Exit lobby button */
  private val exitLobby = new StyledButton("Exit Lobby") {
    onAction = _ => lobbyContext match {
      /** We notify the lobby observer that we exited the lobby */
      case Some(lc) =>
        lc notifyLobbyEvent LobbyEventWrapper(AbortLobby, Some(user))
        upperSceneListener.onLobbyExited()
      case _ =>
    }
  }

  /** Start game button */
  private val startGame = new Button("Start Game") {
    /** Only visible if the user is a server and there are at least two players*/
    styleClass.add("default-button-style")
    if (user.isServer) {
      disable <== isStartGameDisabled
      onAction = _ => {
        if (_levelInfo.isDefined) {
          listener.onStartMultiPlayerGameClick(_levelInfo.get)
        } else throw new IllegalArgumentException("LevelInfo was empty in MultiPlayerLobbyScene, so the server cannot start the game")
      }
    } else {
      visible = false
    }
  }

  /** Requesting a structured layout */
  private val rootLayout: BorderPane = new BorderPane {
    padding = Insets(130)
    alignmentInParent = Pos.Center
    /** Setting the upper MenuBar */
    center = container
    private val bottomContainer = new HBox(30.0, exitLobby) {
      alignment = Pos.Center
    }
    if (user.isServer) bottomContainer.children.add(startGame)
    bottom = bottomContainer
  }

  /** Enabling the layout */
  root = rootLayout

  override def updateUsers(users: Seq[User]): Unit = {
    userList clear()
    userList ++= users.map(_.getUserWithProperty)
    /** Updating the observable property */
    isStartGameDisabled.value_=(userList.size < 2)
    if (isStartGameDisabled.value) {
      startGame.styleClass.remove("enabled-button-style")
    } else {
      startGame.styleClass.add("enabled-button-style")
    }
  }

  override def onMultiPlayerGameStarted(multiPlayerLevelContext: MultiPlayerLevelContext, levelInfo: LevelInfo): Unit = {
    /** Creating a multiplayer level*/
    val multiPlayerLevelScene = new MultiPlayerLevelScene(parentStage, levelInfo, listener, () => parentStage.scene = this)

    multiPlayerLevelContext.setListener(multiPlayerLevelScene)
    multiPlayerLevelScene.levelContext = multiPlayerLevelContext

    Platform.runLater({
      parentStage.scene = multiPlayerLevelScene
    })
  }

  override def onLobbyAborted(): Unit = Platform.runLater({upperSceneListener.onLobbyExited()})

}

/**
  * Trait used by UpperMultiPlayerLobbyScene to notify an event to the upper scene
  */
trait UpperMultiPlayerLobbySceneListener {

  /** Called when the user exits from the lobby */
  def onLobbyExited()

}

/**
  * Trait used by MultiPlayerLobbyScene to notify events which need to be managed by the View
  */
trait MultiPlayerLobbySceneListener extends LevelSceneListener {

  /** Called once per lobby. This will eventually lead to the server init. The server will eventually respond using the previously passed lobby context
    *
    * @param levelInfo the level info requested by the controller
    */
  def onStartMultiPlayerGameClick(levelInfo: LevelInfo)

}
