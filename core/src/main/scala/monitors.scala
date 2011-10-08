package monido

import scala.actors.Actor

case object Pulse 
case object Die

trait MonitorComponent { 
  val monitor: MonitorDevice
  trait MonitorDevice extends Actor {
    def act() {
      loop {
        body
      }
    }
    def body: Unit
  }
  
  trait SimpleMonitorDevice extends MonitorDevice {
    def pulsed: Unit
    def body {
      react {
        case Pulse => pulsed
        case Die => this.exit
      }
    }
  }
}
