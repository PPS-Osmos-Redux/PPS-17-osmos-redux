package it.unibo.osmos.redux.utils

import java.time.Instant

/** Debugging utility */
object Logger {
  private val enable = true

  def log(message: String)(implicit who: String): Unit = {
    if (enable) {
      println(s"[$who] [${Instant.ofEpochMilli(System.currentTimeMillis())}]: $message")
    }
  }
}
