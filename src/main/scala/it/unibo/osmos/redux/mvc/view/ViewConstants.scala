package it.unibo.osmos.redux.mvc.view

import scalafx.scene.paint.Color
import scalafx.stage.Screen

/** View constants */
object ViewConstants {

  /** Window constants */
  object Window {
    val DefaultWindowTitle: String = "Osmos-Redux"
    val DefaultWindowWidth: Double = Screen.primary.visualBounds.width
    val HalfWindowWidth: Double = DefaultWindowWidth / 2
    val DefaultWindowHeight: Double = Screen.primary.visualBounds.height
    val HalfWindowHeight: Double = DefaultWindowHeight / 2
  }

  object Editor {

    import Window._

    val MaxLevelRadius: Double = 0.8 * (if (HalfWindowHeight < HalfWindowWidth) HalfWindowHeight else HalfWindowWidth)
    val StartingLevelRadius: Double = MaxLevelRadius / 2
    val MaxLevelWidth: Double = 0.8 * DefaultWindowWidth
    val MaxLevelHeight: Double = 0.8 * DefaultWindowHeight
    val StartingLevelWidth: Double = DefaultWindowWidth / 2
    val StartingLevelHeight: Double = DefaultWindowHeight / 2
  }

  object Level {
    /** Scrolling delta */
    val ScrollingDelta = 1.1
    /** Max zoom out scale */
    val MaxZoomOutScale = 1.0
    /** Max zoom in scale */
    val MaxZoomInScale = 1.2
  }

  /** Entities constants */
  object Entities {

    /** Colors constants */
    object Colors {
      val DefaultPlayerColor: Color = Color.Green
      val DefaultEntityMaxColor: Color = Color.DarkRed
      val DefaultEntityMinColor: Color = Color.LightBlue
    }

    /** Textures constants */
    object Textures {
      val TextureFolder: String = "/textures/"
      val BackgroundTexture: String = TextureFolder + "background.png"
      val CellTexture: String = TextureFolder + "cell_blue.png"
      val PlayerCellTexture: String = TextureFolder + "cell_green.png"
      val AttractiveTexture: String = TextureFolder + "cell_red.png"
      val RepulsiveTexture: String = TextureFolder + "cell_yellow.png"
      val AntiMatterTexture: String = TextureFolder + "cell_dark_blue.png"
      val SentientTexture: String = TextureFolder + "cell_purple.png"
      val ControllerTexture: String = TextureFolder + "cell_violet.png"
    }

  }

}
