package it.unibo.osmos.redux.utils

/**
  * Class modelling a generic response from a procedure, storing the generic result and a message
  * @param result the result
  * @param message the message
  * @tparam A the result type
  */
case class GenericResponse[A](result: A, message: String) {}
