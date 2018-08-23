package it.unibo.osmos.redux.mvc.view.scenes

import scalafx.beans.property.StringProperty
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.stage.Stage

class MultiPlayerScene(override val parentStage: Stage, val listener: MultiPlayerSceneListener) extends BaseScene(parentStage) {

  private val username: StringProperty = StringProperty("")
  private val usernameTextField = new TextField() {
    maxWidth <== parentStage.width / 4
    username <== text
  }

  /* Requesting a structured layout */
  private val rootLayout: BorderPane = new BorderPane {
    alignmentInParent = Pos.Center
    /* Setting the upper MenuBar */
    center = new VBox(5.0) {

      alignment = Pos.Center


      children = List(usernameTextField, new HBox(10.0, new Button("aaaa"), new Button("eeeee")))

    }
  }

  /* Enabling the layout */
  root = rootLayout


}

trait MultiPlayerSceneListener {

}
