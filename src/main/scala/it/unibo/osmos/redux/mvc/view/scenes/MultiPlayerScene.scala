package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.view.components.custom.{TitledComboBox, TitledTextField}
import scalafx.beans.property.{BooleanProperty, StringProperty}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.stage.Stage

class MultiPlayerScene(override val parentStage: Stage, val listener: MultiPlayerSceneListener, val upperSceneListener: UpperMultiPlayerSceneListener) extends BaseScene(parentStage) {

  private val username: StringProperty = StringProperty("")
  private val usernameTextField = new TitledTextField("Username: ", username)

  private val serverIp: StringProperty = StringProperty("")
  private val serverIpTextField = new TitledTextField("Server IP: ", serverIp)

  private val serverPort: StringProperty = StringProperty("")
  private val serverPortTextField = new TitledTextField("Server port: ", serverPort)

  private val mode: BooleanProperty = BooleanProperty(true)
  private val modeComboBox = new TitledComboBox[String]("Mode: ", Seq("Server", "Client"), {
    case "Server" => mode.value = true; serverIpTextField.root.visible = true; serverPortTextField.root.visible = true;
    case "Client" => mode.value = false; serverIpTextField.root.visible = false; serverPortTextField.root.visible = false;
  }, vertical = false)


  private val goBack = new Button("Go back") {
    onAction = _ => upperSceneListener.onMultiPlayerSceneBackClick()
  }
  private val goToLobby = new Button("Go to lobby") {
    onAction = _ => if (mode.value) listener.onLobbyClickAsServer(username.value, serverIp.value, serverPort.value) else listener.onLobbyClickAsClient(username.value)
  }

  private val container: VBox = new VBox(5.0) {

    maxWidth <== parentStage.width / 4
    maxHeight <== parentStage.height / 4

    alignment = Pos.Center
    children = Seq(usernameTextField.root, modeComboBox.root, serverIpTextField.root, serverPortTextField.root)

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
  * Trait used by MultiPlayerScene to notify an event to the upper scene
  */
trait UpperMultiPlayerSceneListener {

  /**
    * Called when the user wants to go back to the previous screen
    */
  def onMultiPlayerSceneBackClick()
}

/**
  * Trait used by MultiPlayerScene to notify events which need to be managed by the View
  */
trait MultiPlayerSceneListener {

  /**
    * Called when the user wants to go to the lobby as a server
    */
  def onLobbyClickAsServer(username: String, ip: String, port: String)

  /**
    * Called when the user wants to go to the lobby as a client
    */
  def onLobbyClickAsClient(username: String)

}
