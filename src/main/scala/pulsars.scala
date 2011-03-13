package com.github.philcali
package monido

import java.util.{Timer, TimerTask}

trait PulsingComponent {
  val pulsar: PulsarDevice
  trait PulsarDevice {
    def start
    def kill
  }
}

/**
 * A PulsingComponent has a MonitorComponent dependency
 * that will be injected into it
 */
trait PulsingComponentImpl extends PulsingComponent {
  this: MonitorComponent =>
  class Pulsar(interval: Long) extends PulsarDevice {
    val timer = new Timer
    def start = timer.schedule(new TimerTask{
      def run() = monitor ! Pulse
    }, 0, interval)

    def kill = {
      monitor ! Die
      timer.cancel
    }
  }
}
