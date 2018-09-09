package it.unibo.osmos.redux

import it.unibo.osmos.redux.mvc.controller.levels.structure.MapShape
import it.unibo.osmos.redux.mvc.view.components.editor.CircleLevelCreator
import org.scalatest.FunSuite

class TestEditor extends FunSuite {

  //init javafx toolkit
  com.sun.javafx.application.PlatformImpl.startup(() => {
    def foo(): Unit = {}
    foo()
  })

  test("Creator - CircleLevelCreator") {
    val levelCreator: CircleLevelCreator = new CircleLevelCreator()
    levelCreator.xCenter.value = 200
    levelCreator.yCenter.value = 150
    levelCreator.radius.value = 600
    val map = levelCreator.create()
    assert(map.isInstanceOf[MapShape.Circle])
    val circleLevel = map.asInstanceOf[MapShape.Circle]
    assert(circleLevel.center.x == 200)
    assert(circleLevel.center.y == 150)
    assert(circleLevel.radius == 600)
  }

}
