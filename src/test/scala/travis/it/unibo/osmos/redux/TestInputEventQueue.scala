package it.unibo.osmos.redux

import it.unibo.osmos.redux.mvc.view.events.MouseEventWrapper
import it.unibo.osmos.redux.utils.{InputEventQueue, Point}
import org.scalatest.FunSuite

class TestInputEventQueue extends FunSuite {

  val dummyEvent = MouseEventWrapper(Point(1,1))

  test("InputEventQueue should be empty at launch") {
    assert(InputEventQueue.dequeue().isEmpty)
  }

  test("InputEventQueue should not be empty after a push") {
    InputEventQueue.enqueue(dummyEvent)
    assert(InputEventQueue.dequeue().nonEmpty)
    assert(InputEventQueue.dequeue().isEmpty)
  }

  test("InputEventQueue should be empty after a single push and a single pop") {
    InputEventQueue.enqueue(dummyEvent)
    assert(InputEventQueue.dequeue().nonEmpty)
    assert(InputEventQueue.dequeue().isEmpty)
  }

  test("InputEventQueue should be empty after a popAll call") {
    InputEventQueue.enqueue(dummyEvent)
    InputEventQueue.enqueue(dummyEvent)
    InputEventQueue.dequeueAll()
    assert(InputEventQueue.dequeue().isEmpty)
  }
}
