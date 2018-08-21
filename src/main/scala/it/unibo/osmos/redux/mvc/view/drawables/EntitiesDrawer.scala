package it.unibo.osmos.redux.mvc.view.drawables

/**
  * Trait modelling an object which can draw entities
  */
trait EntitiesDrawer {

  /**
    * Called whenever the entities must be drawn
    * @param playerEntity the player entity. It may be empty
    * @param entities the other entities
    */
  def drawEntities(playerEntity: Option[DrawableWrapper], entities: Seq[DrawableWrapper])
}
