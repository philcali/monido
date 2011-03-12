package com.github.philcali
package monido

import java.util.{Timer, TimerTask}
import java.io.File
import scala.actors.Actor

case object Pulse 
case object Die

trait PulsingComponent {
  val pulsar: PulsarDevice
  trait PulsarDevice {
    def start
    def kill
  }
}


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
}

trait MonidoListener[A] {
  def changed(item: A): Unit
}

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

trait FileMonitorImpl extends MonitorComponent {
  class FileMonitor(listener: MonidoListener[File], area: String) extends MonitorDevice {
    def initiate = {
      val current = new File(area)
      if(!current.exists) 
        throw new IllegalArgumentException("%s doesn exist" format(area))
      
      current.isDirectory match {
        case true => files(current)
        case false => Array(current)
      }
    }
    def files(file: File) = file.listFiles.filter(!_.getName.startsWith(".")) 
    def transform = initiate.map(file => (file.getName, file.lastModified))
    def body {
      val old = transform
      react {
        case Pulse => initiate.foreach { file =>
          old.find(_._1 == file.getName) match {
            case Some(found) => if(found._2 != file.lastModified) listener.changed(file)
            case None=> listener.changed(file)
          }
        }
        case Die => this.exit
      }
    }
  }
}

trait Monido extends MonitorComponent with PulsingComponentImpl {
  def start() = {
    monitor.start
    pulsar.start
  }
  def kill() = pulsar.kill
}

trait MonidoFactory[A] {
  def create(what: A, interval: Long, listener: MonidoListener[A]): Monido
  def apply(what: A, interval: Long) (handler: A => Unit): Monido = {
    val listener = new MonidoListener[A] {
      def changed(item: A) = handler(item)
    }
    val monido = create(what, interval, listener)
    monido.start
    monido
  }
}

object FileMonido extends MonidoFactory[File] {
  private def recuresively(dir: File)(body: File => Monido): Unit = {
    val allFiles = dir.listFiles.filter(file => 
                                       !file.getName.startsWith(".") && 
                                        file.isDirectory)
    allFiles.foreach { file => 
      body(file)
      recuresively(file)(body)
    }
  }

  def apply(area: String, interval: Long = 500, recurse: Boolean = false) (handler: File => Unit): Monido = {
    val file = new File(area)
    if(recurse && file.isDirectory) {
      recuresively(file) { file =>
        apply(file, interval)(handler)
      }
    }
    apply(file, interval)(handler)
  }

  def create(file: File, interval: Long, listener: MonidoListener[File]) =
    new Monido with FileMonitorImpl {
      val monitor = new FileMonitor(listener, file.getAbsolutePath)
      val pulsar = new Pulsar(interval)
    }
}

