package it.unibo.osmos.redux.mvc.view.components.instructions

import it.unibo.osmos.redux.mvc.view.components.level.LevelScreen
import it.unibo.osmos.redux.mvc.view.components.level.LevelScreen.LevelScreenImpl
import it.unibo.osmos.redux.mvc.view.scenes.BaseScene

/** Container holding a single instruction screen showing the game controls and instructions
  *
  * @param scene the scene in which the screen is held
  */
class GameInstructionScreen(val scene: BaseScene) {

  /** The instruction screen */
  private val _instructionScreen = LevelScreen.Builder(scene)
    .withText("Game Controls & Instruction", 50)
    .withText("Click on the screen to eject mass and move in the opposite click direction")
    .withText("On collision the bigger cell will absorb the smaller one")
    .withText("Wheel up/down to zoom in/out")
    .withText("Press [esc] to stop game (single player only)")
    .withText("Press [up] or [right] arrow key to speed up game time (single player only)")
    .withText("Press [down] or [left] arrow key to slow down game time (single player only)")
    .withText("Press [i] to show/hide the game controls", 20)
    .build()

  /** Instruction screen getter */
  def instructionScreen: LevelScreenImpl = _instructionScreen

}
