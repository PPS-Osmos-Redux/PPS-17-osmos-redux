package it.unibo.osmos.redux.mvc.view

import scalafx.scene.paint.Color
import scalafx.stage.Screen

/**
  *  View constants
  */
object ViewConstants {

  /**
    * Window constants
    */
  object Window {
    val defaultWindowTitle: String = "Osmos-Redux"
    val defaultWindowWidth: Double = Screen.primary.visualBounds.width
    val defaultWindowHeight: Double = Screen.primary.visualBounds.height
  }

  object Entities {
    object Colors {
      val defaultPlayerColor: Color = Color.Green
      val defaultEntityMaxColor: Color = Color.DarkRed
      val defaultEntityMinColor: Color = Color.LightBlue
    }
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
