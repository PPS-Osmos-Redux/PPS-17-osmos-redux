package it.unibo.osmos.redux.ecs.entities.builders

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.ecs.entities.{CellEntity, EntityType}
import it.unibo.osmos.redux.utils.{Point, Vector}

trait EntityBuilder[T] {

  /**
    * Flag to detect multiple builds
    */
  private var multipleBuildFlag = false

  private var acceleration = AccelerationComponent(0,0)
  private var collidable = CollidableComponent(true)
  private var dimension = DimensionComponent(1)
  private var position = PositionComponent(Point(0,0))
  private var speed = SpeedComponent(0, 0)
  private var visible = VisibleComponent(true)
  private var entityType = TypeComponent(EntityType.Matter)

  /**
    * Sets the collidability.
    * @param collidable If the entity can collide or not.
    * @return The entity builder.
    */
  def collidable(collidable: Boolean): EntityBuilder[T] = {
    this.collidable = CollidableComponent(collidable)
    this
  }

  /**
    * Sets the visibility.
    * @param visible If the entity is visible or not.
    * @return The entity builder.
    */
  def visible(visible: Boolean): EntityBuilder[T] = {
    this.visible = VisibleComponent(visible)
    this
  }

  /**
    * Sets the acceleration.
    * @param x The acceleration x coordinate.
    * @param y The acceleration y coordinate.
    * @return The entity builder.
    */
  def withAcceleration(x: Double, y: Double): EntityBuilder[T] = {
    this.acceleration = AccelerationComponent(x, y)
    this
  }

  /**
    * Sets the acceleration.
    * @param acceleration The acceleration vector.
    * @return The entity builder.
    */
  def withAcceleration(acceleration: Vector): EntityBuilder[T] = {
    this.acceleration = AccelerationComponent(acceleration)
    this
  }

  /**
    * Sets the acceleration.
    * @param acceleration The Acceleration component.
    * @return The entity builder.
    */
  def withAcceleration(acceleration: AccelerationComponent): EntityBuilder[T] = {
    this.acceleration = acceleration.copy()
    this
  }

  /**
    * Sets the dimension.
    * @param dimension The radius of the entity.
    * @return The entity builder.
    */
  def withDimension(dimension: Double): EntityBuilder[T] = {
    this.dimension = DimensionComponent(dimension)
    this
  }

  /**
    * Sets the dimension.
    * @param dimension The dimension component.
    * @return The entity builder.
    */
  def withDimension(dimension: DimensionComponent): EntityBuilder[T] = {
    this.dimension = dimension.copy()
    this
  }

  /**
    * Sets the position.
    * @param x The position x coordinate.
    * @param y The position y coordinate.
    * @return The entity builder.
    */
  def withPosition(x: Double, y: Double): EntityBuilder[T] = {
    this.position = PositionComponent(Point(x, y))
    this
  }

  /**
    * Sets the position.
    * @param position The position point.
    * @return The entity builder.
    */
  def withPosition(position: Point): EntityBuilder[T] = {
    this.position = PositionComponent(position)
    this
  }

  /**
    * Sets the position.
    * @param position The position component.
    * @return The entity builder.
    */
  def withPosition(position: PositionComponent): EntityBuilder[T] = {
    this.position = position.copy()
    this
  }

  /**
    * Sets the speed.
    * @param x The speed x coordinate.
    * @param y The speed y coordinate.
    * @return The entity builder.
    */
  def withSpeed(x: Double, y: Double): EntityBuilder[T] = {
    this.speed = SpeedComponent(x, y)
    this
  }

  /**
    * Sets the speed.
    * @param speed The speed component.
    * @return The entity builder.
    */
  def withSpeed(speed: Vector): EntityBuilder[T] = {
    this.speed = SpeedComponent(speed)
    this
  }

  /**
    * Sets the speed.
    * @param speed The speed component.
    * @return The entity builder.
    */
  def withSpeed(speed: SpeedComponent): EntityBuilder[T] = {
    this.speed = speed.copy()
    this
  }

  /**
    *  Sets the entity type.
    * @param entityType The entity type.
    * @return The entity builder.
    */
  def withEntityType(entityType: EntityType.Value): EntityBuilder[T] = {
    this.entityType = TypeComponent(entityType)
    this
  }

  /**
    * Checks if the build method is called multiple times.
    */
  private def checkMultipleBuild(): Unit = {
    if (multipleBuildFlag) {
      throw new UnsupportedOperationException("The builder cannot build multiple times.")
    } else {
      multipleBuildFlag = true
    }
  }

  /**
    * Builds the base cell entity, checks for multiple builds.
    * @return The base cell entity.
    */
  protected def buildBaseCell(): CellEntity = {
    checkMultipleBuild()
    CellEntity(acceleration, collidable, dimension, position, speed, visible, entityType)
  }

  /**
    * Builds the entity.
    * @return The entity.
    */
  def build: T
}
