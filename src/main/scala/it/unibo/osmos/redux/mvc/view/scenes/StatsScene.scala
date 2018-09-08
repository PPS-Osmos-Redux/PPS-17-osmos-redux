package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.levels.structure.CampaignLevel
import it.unibo.osmos.redux.mvc.view.ViewConstants
import scalafx.geometry.Pos
import scalafx.scene.control.TableView
import scalafx.scene.layout.VBox
import scalafx.stage.Stage

class StatsScene(override val parentStage: Stage, listener: StatsSceneListener, previousSceneListener: BackClickListener) extends DefaultBackScene(parentStage, previousSceneListener) {

  private val statsTable = new TableView[String]() {
    maxWidth = ViewConstants.Window.halfWindowWidth
    prefHeight = ViewConstants.Window.defaultWindowHeight / 4
  }

  /**
    * The central container
    */
  protected val container: VBox = new VBox(15) {
    alignment = Pos.Center
    children = Seq(statsTable, goBack)
    styleClass.add("settings-vbox")
  }

  /* Setting the root container*/
  root = container
}

trait StatsSceneListener {

  /** This method retrieve the campaign level info
    *
    * @return a list of CampaignLevel
    */
  def getCampaignLevels: List[CampaignLevel]
}
