package it.unibo.osmos.redux.mvc.view.components.instructions

import it.unibo.osmos.redux.mvc.view.components.level.LevelScreen
import it.unibo.osmos.redux.mvc.view.components.level.LevelScreen.LevelScreenImpl
import it.unibo.osmos.redux.mvc.view.scenes.BaseScene
import scalafx.scene.paint.Color

/** Instruction screen player in the editor
  *
  * @param scene the scene
  */
class EditorInstructionScreen(val scene: BaseScene) {

  /** The instruction screen */
  private val _instructionScreen = LevelScreen.Builder(scene)
    .withText("Instructions", 50, Color.White)
    .withText("Press [Ctrl] to toggle the placeholder visibility")
    .withText("Configure the desired entities on the left panel")
    .withText("Configure the desired level, victory rule and collision rule on the right panel")
    .withText("When the placeholder is visible, click to insert a new entity on the level")
    .withText("Press [H] to show/hide the instructions screen", 20, Color.White)
    .build()

  /** Instruction screen getter */
  def instructionScreen: LevelScreenImpl = _instructionScreen
}
