package it.unibo.osmos.redux.ecs.components

/**
  * Component for entity's type
  * @param typeEntity entity's type
  */
case class TypeComponent(typeEntity: EntityType.Value) extends Component {

  override def copy(): TypeComponent = TypeComponent(typeEntity)
}
