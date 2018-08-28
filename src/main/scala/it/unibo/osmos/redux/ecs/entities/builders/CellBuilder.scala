package it.unibo.osmos.redux.ecs.entities.builders

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityType}
import it.unibo.osmos.redux.utils.Point

/** Builder for Cell Entities */
class CellBuilder() {

  /**
    * Flag to detect multiple builds
    */
  protected var multipleBuildFlag = false

  protected var acceleration = AccelerationComponent(0,0)
  protected var collidable = CollidableComponent(true)
  protected var dimension = DimensionComponent(1)
  protected var position = PositionComponent(Point(0,0))
  protected var speed = SpeedComponent(0, 0)
  protected var visible = VisibleComponent(true)
  protected var entityType = TypeComponent(EntityType.Matter)

  def collidable(collidable: Boolean): CellBuilder = {
    this.collidable = CollidableComponent(collidable)
    this
  }

  def visible(visible: Boolean): CellBuilder = {
    this.visible = VisibleComponent(visible)
    this
  }

  def withAcceleration(x: Double, y: Double): CellBuilder = {
    this.acceleration = AccelerationComponent(x, y)
    this
  }

  def withAcceleration(acceleration: AccelerationComponent): CellBuilder = {
    this.acceleration = AccelerationComponent(acceleration.vector.x, acceleration.vector.y)
    this
  }

  def withDimension(dimension: Double): CellBuilder = {
    this.dimension = DimensionComponent(dimension)
    this
  }

  def withDimension(dimension: DimensionComponent): CellBuilder = {
    this.dimension = DimensionComponent(dimension.radius)
    this
  }

  def withPosition(x: Double, y: Double): CellBuilder = {
    this.position = PositionComponent(Point(x, y))
    this
  }

  def withPosition(position: Point): CellBuilder = {
    this.position = PositionComponent(Point(position.x, position.y))
    this
  }

  def withPosition(position: PositionComponent): CellBuilder = {
    this.position = PositionComponent(Point(position.point.x, position.point.y))
    this
  }

  def withSpeed(x: Double, y: Double): CellBuilder = {
    this.speed = SpeedComponent(x, y)
    this
  }

  def withSpeed(speed: SpeedComponent): CellBuilder = {
    this.speed = SpeedComponent(speed.vector.x, speed.vector.y)
    this
  }

  def withEntityType(entityType: EntityType.Value): CellBuilder = {
    this.entityType = TypeComponent(entityType)
    this
  }

  protected def checkMultipleBuild(): Unit = {
    if (multipleBuildFlag) {
      throw new UnsupportedOperationException("The builder cannot build multiple times.")
    } else {
      multipleBuildFlag = true
    }
  }

  def build: CellEntity = {
    checkMultipleBuild()
    CellEntity(acceleration, collidable, dimension, position, speed, visible, entityType)
  }
}
