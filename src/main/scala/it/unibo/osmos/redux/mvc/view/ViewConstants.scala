package it.unibo.osmos.redux.mvc.view

import it.unibo.osmos.redux.mvc.view.ViewConstants.Window.halfWindowHeight
import scalafx.scene.paint.Color
import scalafx.stage.Screen

/**
  *  View constants
  */
object ViewConstants {

  /** Window constants */
  object Window {
    val defaultWindowTitle: String = "Osmos-Redux"
    val defaultWindowWidth: Double = Screen.primary.visualBounds.width
    val halfWindowWidth: Double = defaultWindowWidth / 2
    val defaultWindowHeight: Double = Screen.primary.visualBounds.height
    val halfWindowHeight: Double = defaultWindowHeight / 2
  }

  object Editor {
    import Window._
    val maxLevelRadius: Double = 0.8 * (if (halfWindowHeight < halfWindowWidth) halfWindowHeight else halfWindowWidth)
    val startingLevelRadius: Double = maxLevelRadius / 2
    val maxLevelWidth: Double = 0.8 * defaultWindowWidth
    val maxLevelHeight: Double = 0.8 * defaultWindowHeight
    val startingLevelWidth: Double = defaultWindowWidth / 2
    val startingLevelHeight: Double = defaultWindowHeight / 2
  }

  /** Entities constants */
  object Entities {
    /** Colors constants */
    object Colors {
      val defaultPlayerColor: Color = Color.Green
      val defaultEntityMaxColor: Color = Color.DarkRed
      val defaultEntityMinColor: Color = Color.LightBlue
    }
    /** Textures constants */
    object Textures {
      val textureFolder: String = "/textures/"
      val backgroundTexture: String = textureFolder + "background.png"
      val cellTexture: String = textureFolder + "cell_blue.png"
      val playerCellTexture: String = textureFolder + "cell_green.png"
      val attractiveTexture: String = textureFolder + "cell_red.png"
      val repulsiveTexture: String = textureFolder + "cell_yellow.png"
      val antiMatterTexture: String = textureFolder + "cell_dark_blue.png"
      val sentientTexture: String = textureFolder + "cell_purple.png"
      val controllerTexture: String = textureFolder + "cell_violet.png"
    }

  }

}
