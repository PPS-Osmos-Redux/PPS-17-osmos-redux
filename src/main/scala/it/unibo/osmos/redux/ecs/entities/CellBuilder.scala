package it.unibo.osmos.redux.ecs.entities

import it.unibo.osmos.redux.ecs.components._
import it.unibo.osmos.redux.utils.{Point, Vector}

/**Base builder for all type of cell*/
case class CellBuilder() {

  // Flag to detect multiple builds
  private var multipleBuildFlag = false

  private var acceleration = AccelerationComponent(0,0)
  private var collidable = CollidableComponent(true)
  private var dimension = DimensionComponent(1)
  private var position = PositionComponent(Point(0,0))
  private var speed = SpeedComponent(0, 0)
  private var visible = VisibleComponent(true)
  private var entityType = TypeComponent(EntityType.Matter)
  private var specificWeight: SpecificWeightComponent = SpecificWeightComponent(1)
  private var spawner = SpawnerComponent(false)

  /** Sets the collidability.
    * @param collidable If the entity can collide or not.
    * @return The entity builder.
    */
  def collidable(collidable: Boolean): CellBuilder = {
    this.collidable = CollidableComponent(collidable)
    this
  }

  /** Sets the visibility.
    * @param visible If the entity is visible or not.
    * @return The entity builder.
    */
  def visible(visible: Boolean): CellBuilder = {
    this.visible = VisibleComponent(visible)
    this
  }

  /** Sets the acceleration.
    * @param x The acceleration x coordinate.
    * @param y The acceleration y coordinate.
    * @return The entity builder.
    */
  def withAcceleration(x: Double, y: Double): CellBuilder = {
    this.acceleration = AccelerationComponent(x, y)
    this
  }

  /** Sets the acceleration.
    * @param acceleration The acceleration vector.
    * @return The entity builder.
    */
  def withAcceleration(acceleration: Vector): CellBuilder = {
    this.acceleration = AccelerationComponent(acceleration)
    this
  }

  /** Sets the acceleration.
    * @param acceleration The Acceleration component.
    * @return The entity builder.
    */
  def withAcceleration(acceleration: AccelerationComponent): CellBuilder = {
    this.acceleration = acceleration.copy()
    this
  }

  /** Sets the dimension.
    * @param dimension The radius of the entity.
    * @return The entity builder.
    */
  def withDimension(dimension: Double): CellBuilder = {
    this.dimension = DimensionComponent(dimension)
    this
  }

  /** Sets the dimension.
    * @param dimension The dimension component.
    * @return The entity builder.
    */
  def withDimension(dimension: DimensionComponent): CellBuilder = {
    this.dimension = dimension.copy()
    this
  }

  /** Sets the position.
    * @param x The position x coordinate.
    * @param y The position y coordinate.
    * @return The entity builder.
    */
  def withPosition(x: Double, y: Double): CellBuilder = {
    this.position = PositionComponent(Point(x, y))
    this
  }

  /** Sets the position.
    * @param position The position point.
    * @return The entity builder.
    */
  def withPosition(position: Point): CellBuilder = {
    this.position = PositionComponent(position)
    this
  }

  /** Sets the position.
    * @param position The position component.
    * @return The entity builder.
    */
  def withPosition(position: PositionComponent): CellBuilder = {
    this.position = position.copy()
    this
  }

  /** Sets the speed.
    * @param x The speed x coordinate.
    * @param y The speed y coordinate.
    * @return The entity builder.
    */
  def withSpeed(x: Double, y: Double): CellBuilder = {
    this.speed = SpeedComponent(x, y)
    this
  }

  /** Sets the speed.
    * @param speed The speed component.
    * @return The entity builder.
    */
  def withSpeed(speed: Vector): CellBuilder = {
    this.speed = SpeedComponent(speed)
    this
  }

  /** Sets the speed.
    * @param speed The speed component.
    * @return The entity builder.
    */
  def withSpeed(speed: SpeedComponent): CellBuilder = {
    this.speed = speed.copy()
    this
  }

  /** Sets the entity type.
    * @param entityType The entity type.
    * @return The entity builder.
    */
  def withEntityType(entityType: EntityType.Value): CellBuilder = {
    this.entityType = TypeComponent(entityType)
    this
  }

  /** Sets the specific weight.
    * @param weight The weight.
    * @return The entity builder.
    */
  def withSpecificWeight(weight: Double): CellBuilder = {
    this.specificWeight = SpecificWeightComponent(weight)
    this
  }

  /** Sets the specific weight.
    * @param weight The specific weight component.
    * @return The entity builder.
    */
  def withSpecificWeight(weight: SpecificWeightComponent): CellBuilder = {
    this.specificWeight = weight.copy()
    this
  }

  /** Sets the spawner.
    * @param canSpawn If the entity spawner can spawn or not.
    * @return The entity builder.
    */
  def withSpawner(canSpawn: Boolean): CellBuilder = {
    this.spawner = SpawnerComponent(canSpawn)
    this
  }

  /** Sets the spawner.
    * @param spawner The spawner component.
    * @return The entity builder.
    */
  def withSpawner(spawner: SpawnerComponent): CellBuilder = {
    this.spawner = spawner.copy()
    this
  }

  //checks if the build method is called multiple times.
  private def checkMultipleBuild(): Unit = {
    if (multipleBuildFlag) {
      throw new UnsupportedOperationException("The builder cannot build multiple times.")
    } else {
      multipleBuildFlag = true
    }
  }

  /** Builds the base cell entity, checks for multiple builds.
    * @return The base cell entity.
    */
  private def buildBaseCell(): CellEntity = {
    checkMultipleBuild()
    CellEntity(acceleration, collidable, dimension, position, speed, visible, entityType)
  }

  /** Builds a CellEntity.
    * @return The CellEntity.
    */
  def buildCellEntity(): CellEntity = buildBaseCell()

  /** Builds a GravityCellEntity.
    * @return The GravityCellEntity.
    */
  def buildGravityEntity(): GravityCellEntity = {
    if(!(Seq(EntityType.Attractive, EntityType.Repulsive) contains entityType.typeEntity)) {
      withEntityType(EntityType.Attractive)
    }
    GravityCellEntity(buildBaseCell(), specificWeight)
  }

  /** Builds a PlayerCellEntity.
    * @return The PlayerCellEntity.
    */
  def buildPlayerEntity(): PlayerCellEntity = {
    withEntityType(EntityType.Controlled)
    PlayerCellEntity(buildBaseCell(), spawner)
  }

  /** Builds a SentientCellEntity.
    * @return The SentientCellEntity.
    */
  def buildSentientEntity(): SentientCellEntity = {
    withEntityType(EntityType.Sentient)
    SentientCellEntity(buildBaseCell(), spawner)
  }
}
