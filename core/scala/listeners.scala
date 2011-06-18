package com.github.philcali
package monido

/**
# Listeners

Listeners are highly customizeable. I have more plans for these
guys in the future.
*/
trait ListeningComponent[A] {
  val listener: MonidoListener[A]
}

trait MonidoListener[A] {
  def changed(item: A): Unit
}
