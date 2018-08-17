package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.utils.Point

case class CellBuilder() {
  var acceleration = AccelerationComponent(0,0)
  var collidable = CollidableComponent(true)
  var dimension = DimensionComponent(0)
  var position = PositionComponent(Point(0,0))
  var speed = SpeedComponent(0, 0)
  var visible = VisibleComponent(true)
  var entityType = TypeComponent(EntityType.Material)

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
    this.acceleration = AccelerationComponent(acceleration.accelerationX, acceleration.accelerationY)
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
    this.speed = SpeedComponent(speed.speedX, speed.speedY)
    this
  }

  def withEntityType(entityType: EntityType.Value): CellBuilder = {
    this.entityType = TypeComponent(entityType)
    this
  }

  def build: CellEntity = CellEntity(acceleration, collidable, dimension, position, speed, visible, entityType)
}