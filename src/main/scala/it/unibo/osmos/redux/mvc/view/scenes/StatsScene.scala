package it.unibo.osmos.redux.mvc.view.scenes

import it.unibo.osmos.redux.mvc.controller.levels.structure.CampaignLevel
import it.unibo.osmos.redux.mvc.view.ViewConstants
import javafx.collections.FXCollections
import scalafx.beans.property.StringProperty
import scalafx.geometry.Pos
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.scene.layout.VBox
import scalafx.stage.Stage

/** Scene where the user can find his in game stats
  *
  * @param parentStage the parent stage
  * @param listener the StatsSceneListener
  * @param previousSceneListener the BackClickListener
  */
class StatsScene(override val parentStage: Stage, listener: StatsSceneListener, previousSceneListener: BackClickListener) extends DefaultBackScene(parentStage, previousSceneListener) {

  private implicit def toStringProperty[A](value: A): StringProperty = StringProperty(value.toString)

  private val levelNameColumn = new TableColumn[CampaignLevel, String]("Level Name") {
    cellValueFactory = p => p.value.levelInfo.name
  }
  private val victoryRuleColumn = new TableColumn[CampaignLevel, String]("Victory Rule") {
    cellValueFactory = p => p.value.levelInfo.victoryRule
  }
  private val victoriesColumn = new TableColumn[CampaignLevel, String]("Victories") {
    cellValueFactory = p => p.value.levelStat.victories
  }
  private val defeatsColumn = new TableColumn[CampaignLevel, String]("Defeats") {
    cellValueFactory = p => p.value.levelStat.defeats
  }

  private val playerData = FXCollections.observableArrayList[CampaignLevel]()
  listener.getCampaignLevels.foreach(e => playerData.add(e))

  private val statsTable = new TableView[CampaignLevel]() {
    maxWidth = ViewConstants.Window.HalfWindowWidth
    prefHeight = ViewConstants.Window.DefaultWindowHeight / 4
    /* add the columns to table*/
    columns ++= List(levelNameColumn, victoryRuleColumn, victoriesColumn, defeatsColumn)
  }

  private val columnMinWidth = statsTable.width / 4.1
  levelNameColumn.minWidth <== columnMinWidth
  victoryRuleColumn.minWidth <== columnMinWidth
  victoriesColumn.minWidth <== columnMinWidth
  defeatsColumn.minWidth <== columnMinWidth

  /* add player data to table */
  statsTable.setItems(playerData)

  /** The central container */
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
