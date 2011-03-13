package com.github.philcali
package monido

trait ListeningComponent[A] {
  val listener: MonidoListener[A]
}

trait MonidoListener[A] {
  def changed(item: A): Unit
}
