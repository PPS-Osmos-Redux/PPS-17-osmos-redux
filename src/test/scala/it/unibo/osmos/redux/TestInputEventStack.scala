package it.unibo.osmos.redux

import it.unibo.osmos.redux.utils.{InputEventStack, Point}
import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import org.scalatest.FunSuite

class TestInputEventStack extends FunSuite {

  val dummyEvent = MouseEventWrapper(Point(1,1))

  test("InputEventStack should be empty at launch") {
    assert(InputEventStack.pop().isEmpty)
  }

  test("InputEventStack should be empty after a single push and a single pop") {
    InputEventStack.push(dummyEvent)
    assert(InputEventStack.pop().nonEmpty)
    assert(InputEventStack.pop().isEmpty)
  }

  test("InputEventStack should be empty after a popAll call") {
    InputEventStack.push(dummyEvent)
    InputEventStack.push(dummyEvent)
    InputEventStack.popAll()
    assert(InputEventStack.pop().isEmpty)
  }
}
