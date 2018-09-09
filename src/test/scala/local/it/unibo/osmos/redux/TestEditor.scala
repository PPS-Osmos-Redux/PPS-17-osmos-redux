package it.unibo.osmos.redux

import it.unibo.osmos.redux.ecs.entities.EntityType
import it.unibo.osmos.redux.mvc.controller.levels.structure.MapShape
import it.unibo.osmos.redux.mvc.view.components.editor._
import it.unibo.osmos.redux.utils.Point
import it.unibo.osmos.redux.utils.Vector
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

  test("Creator - RectangularLevelCreator") {
    val levelCreator: RectangleLevelCreator = new RectangleLevelCreator()
    levelCreator.xCenter.value = 200
    levelCreator.yCenter.value = 150
    levelCreator.levelWidth.value = 600
    levelCreator.levelHeight.value = 400
    val map = levelCreator.create()
    assert(map.isInstanceOf[MapShape.Rectangle])
    val rectangleLevel = map.asInstanceOf[MapShape.Rectangle]
    assert(rectangleLevel.center.x == 200)
    assert(rectangleLevel.center.y == 150)
    assert(rectangleLevel.base == 600)
    assert(rectangleLevel.height == 400)
  }

  test("Creator - CellEntityCreator") {
    val cellEntityCreator = new CellEntityCreator
    cellEntityCreator.x.value = 50
    cellEntityCreator.y.value = 100
    cellEntityCreator.radius.value = 150
    cellEntityCreator.entityType_=(EntityType.Matter)
    cellEntityCreator.xSpeed.value = 60
    cellEntityCreator.ySpeed.value = 100
    cellEntityCreator.xAcceleration.value = 50
    cellEntityCreator.yAcceleration.value = 20
    val entity = cellEntityCreator.create()
    assert(entity.getPositionComponent.point == Point(50, 100))
    assert(entity.getDimensionComponent.radius == 150)
    assert(entity.getSpeedComponent.vector == Vector(60, 100))
    assert(entity.getAccelerationComponent.vector == Vector(50, 20))
    assert(entity.getTypeComponent.typeEntity == EntityType.Matter)
  }

  test("Creator - GravityCellEntityCreator") {
    val cellEntityCreator = new GravityCellEntityCreator
    cellEntityCreator.weight.value = 0.5
    val gravityEntity = cellEntityCreator.create()
    assert(gravityEntity.getSpecificWeightComponent.specificWeight == 0.5)
    assert(gravityEntity.getTypeComponent.typeEntity == EntityType.Attractive)

    /** Changing to Repulsive */
    cellEntityCreator.entityType_=(EntityType.Repulsive)
    val repulsiveEntity = cellEntityCreator.create()
    assert(repulsiveEntity.getTypeComponent.typeEntity == EntityType.Repulsive)
  }

}
