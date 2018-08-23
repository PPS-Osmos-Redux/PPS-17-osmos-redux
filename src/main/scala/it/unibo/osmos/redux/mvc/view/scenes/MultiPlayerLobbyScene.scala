package it.unibo.osmos.redux.mvc.view.scenes

import scalafx.beans.property.StringProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.stage.Stage

/**
  * Lobby showing other clients or servers playing in multiplayer
  * @param parentStage the parent stage
  * @param listener the MultiPlayerLobbySceneListener
  * @param isServer true if the user registered as a server, false otherwise
  */
class MultiPlayerLobbyScene(override val parentStage: Stage, val listener: MultiPlayerLobbySceneListener, val isServer: Boolean) extends BaseScene(parentStage) {

  /**
    * User class
    * @param username the username
    * @param ip the ip
    * @param port the port
    */
  case class User(username: StringProperty, ip: StringProperty, port: StringProperty)

  /**
    * Implicit definition of a User with String arguments
    * @param username the username String
    * @param ip the ip String
    * @param port the port String
    * @return the User with the requeste StringProperties
    */
  implicit def User(username: String, ip: String, port: String): User = User(StringProperty(username), StringProperty(ip), StringProperty(port))

  private val userList = ObservableBuffer[User](
    User("Marco", "0.0.0.0", "0000"),
    User("Davide", "0.0.0.1", "0001"),
    User("Placu", "0.0.0.2", "0002"),
    User("Turi", "0.0.0.3", "0003"),
    User("Proc", "0.0.0.4", "0004"),
  )

  val usersTable: TableView[User] = new TableView[User](userList) {
    columns ++= List(
      new TableColumn[User, String]() {
        text = "Username"
        cellValueFactory = {_.value.username}
      }, new TableColumn[User, String]() {
        text = "IP"
        cellValueFactory = {_.value.ip}
      }, new TableColumn[User, String]() {
        text = "Port"
        cellValueFactory = {_.value.port}
      }
    )
  }

  private val container: VBox = new VBox(5.0) {

    maxWidth <== parentStage.width / 4
    maxHeight <== parentStage.height / 4

    alignment = Pos.Center
    children = Seq(usersTable)

  }

  private val goBack = new Button("Go back") {
    onAction = _ => {}
  }
  private val goToLobby = new Button("Go to lobby") {
    visible = isServer
    onAction = _ => {}
  }

  /* Requesting a structured layout */
  private val rootLayout: BorderPane = new BorderPane {
    padding = Insets(130)
    alignmentInParent = Pos.Center
    /* Setting the upper MenuBar */
    center = container
    bottom = new HBox(30.0, goBack, goToLobby) {
      alignment = Pos.Center
    }
  }

  /* Enabling the layout */
  root = rootLayout

}

/**
  * Trait used by MultiPlayerLobbyScene to notify events which need to be managed by the View
  */
trait MultiPlayerLobbySceneListener {

}
