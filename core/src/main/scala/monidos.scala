package monido

trait Monido extends MonitorComponent with PulsingComponent {
  def start() = {
    monitor.start
    pulsar.start
  }
  def kill() = pulsar.kill
}

trait MonidoFactory[A, B] {
  def create(what: A, interval: Long, handler: B => Unit): Monido
  def apply(what: A, interval: Long) (handler: B => Unit): Monido = {
    val monido = create(what, interval, handler)
    monido.start
    monido
  }
}
