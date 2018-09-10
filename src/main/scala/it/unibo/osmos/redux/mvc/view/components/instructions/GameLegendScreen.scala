package it.unibo.osmos.redux.mvc.view.components.instructions

import it.unibo.osmos.redux.mvc.view.ViewConstants.Entities.Textures._
import it.unibo.osmos.redux.mvc.view.ViewConstants.Window._
import it.unibo.osmos.redux.mvc.view.components.custom.EntityDescriptionBox
import it.unibo.osmos.redux.mvc.view.components.level.LevelScreen
import it.unibo.osmos.redux.mvc.view.components.level.LevelScreen.LevelScreenImpl
import it.unibo.osmos.redux.mvc.view.loaders.ImageLoader
import it.unibo.osmos.redux.mvc.view.scenes.BaseScene
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.layout.HBox

/** Container holding a single legend screen showing the game entities
  *
  * @param scene the scene in which the screen is held
  */
class GameLegendScreen(val scene: BaseScene) {

  private class BaseHBox extends HBox() {
    margin = Insets(10.0, 0.0, 10.0, 0.0)
    prefWidth = defaultWindowWidth
    minWidth = defaultWindowWidth
    maxWidth = defaultWindowWidth
    alignment = Pos.CenterLeft
  }

  private val playerBox = new BaseHBox {
    alignment = Pos.Center
    margin = Insets(0.0, 0.0, 50.0, 0.0)
    children = Seq(new EntityDescriptionBox(ImageLoader.getImage(playerCellTexture), "[Player]\n\nThis is your cell") {
      alignment = Pos.Center
    })
  }

  private val firstBox = new BaseHBox {
    children = Seq(new EntityDescriptionBox(ImageLoader.getImage(cellTexture), "[Matter]\n\nA simple enemy cell"),
      new EntityDescriptionBox(ImageLoader.getImage(antiMatterTexture), "[AntiMatter]\n\nTouching this cell will reduce your size"))
  }

  private val secondBox = new BaseHBox {
    children = Seq(new EntityDescriptionBox(ImageLoader.getImage(attractiveTexture), "[Attractive]\n\nThis cell will attract other cells"),
      new EntityDescriptionBox(ImageLoader.getImage(repulsiveTexture), "[Repulsive]\n\nThis cell will repulse other cells away"))
  }

  private val thirdBox = new BaseHBox {
    children = Seq(new EntityDescriptionBox(ImageLoader.getImage(sentientTexture), "[Sentient]\n\nThis cell will try to win the game"),
      new EntityDescriptionBox(ImageLoader.getImage(controllerTexture), "[Controlled]\n\nThis cell is controlled by another player"))
  }

  /** The legend screen */
  private val _legendScreen = LevelScreen.Builder(scene)
    .withNode(playerBox)
    .withNode(firstBox)
    .withNode(secondBox)
    .withNode(thirdBox)
    .build()

  /** Legend screen getter */
  def legendScreen: LevelScreenImpl = _legendScreen

}
