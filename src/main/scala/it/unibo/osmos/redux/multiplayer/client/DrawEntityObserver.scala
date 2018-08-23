package it.unibo.osmos.redux.multiplayer.client

import it.unibo.osmos.redux.mvc.view.drawables.DrawableWrapper

trait DrawEntityObserver {

  /**
    * Signals to the observer new entities to draw.
    * @param entities The entities to draw.
    */
  def update(entities: Seq[DrawableWrapper])
}
