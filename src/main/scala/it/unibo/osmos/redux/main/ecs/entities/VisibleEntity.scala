package it.unibo.osmos.redux.main.ecs.entities

import it.unibo.osmos.redux.main.ecs.components.Visible

trait VisibleEntity {

  def getVisibleComponent: Visible
}
