package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.multiplayer.common.NetworkUtils
import it.unibo.osmos.redux.mvc.view.components.custom.{TitledComboBox, TitledNumericField, TitledTextField}
import it.unibo.osmos.redux.mvc.view.components.multiplayer.User
import it.unibo.osmos.redux.mvc.view.context.LobbyContext
import scalafx.application.Platform
import scalafx.beans.property.{BooleanProperty, IntegerProperty, StringProperty}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Alert, Button}
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.stage.Stage

/**
  * Scene in which the user can create or join a lobby as a server or as a client
  * @param parentStage the parent stage
  * @param listener the MultiPlayerSceneListener
  * @param upperSceneListener the UpperMultiPlayerSceneListener
  */
class MultiPlayerScene(override val parentStage: Stage, val listener: MultiPlayerSceneListener, val upperSceneListener: UpperMultiPlayerSceneListener) extends BaseScene(parentStage) {

  private val username: StringProperty = StringProperty("")
  private val usernameTextField = new TitledTextField("Username: ", username)

  private val addressTitle: StringProperty = StringProperty("Server address: ")
  private val addressValue: StringProperty = StringProperty("")
  private val addressTextField = new TitledTextField(addressTitle, addressValue)

  private val portTitle: StringProperty = StringProperty("Server port: ")
  private val portValue: IntegerProperty = IntegerProperty(0)
  private val portTextField = new TitledNumericField(portTitle, portValue) {
    minValue = 0
    maxValue = 65535
  }

  private val mode: BooleanProperty = BooleanProperty(false) //default client
  private val modeComboBox = new TitledComboBox[String]("Mode: ", Seq("Client", "Server"), {
    case "Client" =>
      mode.value = false
      addressTitle.setValue("Server address:")
      portTitle.setValue("Server port:")
      if (addressValue.getValue.equals(NetworkUtils.getLocalIPAddress)) addressValue.setValue("")
      portTextField.node.setText("0")
    case "Server" =>
      mode.value = true
      addressTitle.setValue("Address:")
      portTitle.setValue("Port:")
      if (addressValue.isEmpty.get()) addressValue.setValue(NetworkUtils.getLocalIPAddress)
      if (!portTextField.node.getText.equals("0")) portTextField.node.setText("0")
  }, vertical = false)

  private val goBack = new Button("Go back") {
    onAction = _ => upperSceneListener.onMultiPlayerSceneBackClick()
  }

  /**
    * Result parsing function.
    * @return a function which will send the user to the MultiPlayerLobbyScene if the result is true, showing an error otherwise
    */
  private def onLobbyEnterResult: (User, LobbyContext, Boolean) => Unit = (user, lobbyContext, result) => {
    Platform.runLater({
      if (result) {
        /* Creating an abstract listener on the run */
        val lobbySceneListener: UpperMultiPlayerLobbySceneListener = () => parentStage.scene = MultiPlayerScene.this
        /* If the lobby was successfully created, we link the resulting lobby context and go to the next scene */
        val multiPlayerLobbyScene = new MultiPlayerLobbyScene(parentStage, listener, lobbySceneListener, user)
        /* We link the lobby context */
        multiPlayerLobbyScene.lobbyContext_=(lobbyContext)
        /* We go to the next scene */
        parentStage.scene = multiPlayerLobbyScene

      } else {
        /* If an error occurred */
        val alert = new Alert(Alert.AlertType.Error) {
          title = "Error"
          contentText.value = "Error during lobby creation. Please try again later."
        }
        alert.showAndWait()
      }
    })
  }

  private val goToLobby = new Button("Go to lobby") {
    onAction = _ => {
      /* We create the User */
      val user = User(username.value, addressValue.value, portValue.value, isServer = mode.value)
      /* We create the lobby context */
      val lobbyContext = LobbyContext()
      /* We ask to enter in the lobby */
      listener.onLobbyClick(user, lobbyContext, onLobbyEnterResult)
    }
  }

  private val container: VBox = new VBox(5.0) {

    maxWidth <== parentStage.width / 4
    maxHeight <== parentStage.height / 4

    alignment = Pos.Center
    children = Seq(usernameTextField.root, modeComboBox.root, addressTextField.root, portTextField.root)

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
trait MultiPlayerSceneListener extends MultiPlayerLobbySceneListener{

  /**
    * Called when the user wants to go to the lobby as a server
    * @param user the user requesting to enter the lobby
    * @param lobbyContext the lobby context, which may be used by the server to configure existing lobby users
    * @param callback the callback
    */
  def onLobbyClick(user: User, lobbyContext: LobbyContext, callback: (User, LobbyContext, Boolean) => Unit)

}
