package com.github.philcali
package monido

import scala.actors.Actor
import Actor.{self, actor}
import java.io.File

object PullFiles {
  def unapply(file: File) = {
    if(file.isDirectory) Some(file.listFiles.filter(!_.getName.startsWith(".")))
    else Some(Array(file))
  }
}

case object Watch
case object Die
case object Pulse

class Monido(area: String, handler: String => Unit, interval: Long) extends Actor {  
  initiate(area)

  def act() {
    this ! Pulse

    pulse(time, monido) { child => 
      ? match {
        case Pulse => child ! Watch 
        case Die => this.exit
      }
    }
  }
  
  def time = System.currentTimeMillis

  private def pulse(start: Long, child: Actor)(body: Actor => Unit) {
    val now = time
    if(now >= start + interval) {
      body(child)
      pulse(now, monido)(body)
    } else {
      pulse(start, child)(body)
    }
  }

  private def transform(files: Array[File]) = files.map { file =>
    (file.getName, file.lastModified)
  }

  private def initiate(area: String) = {
    val current = new File(area)
    
    if(!current.exists)
      throw new IllegalArgumentException("%s does not exist" format(area))
    
    current
  }

  private def monitor(old: Array[(String, Long)]) {
    initiate(area) match {
      case PullFiles(files) => 
        files.foreach { file =>
          old.find(_._1 == file.getName) match {
            case Some(found) => if(found._2 != file.lastModified) handler(file.getAbsolutePath)
            case None => handler(file.getAbsolutePath)
          }
        }
    }
  }

  private def monido = actor{
    val PullFiles(files) = initiate(area)
    val transformed = transform(files)
    self.react {
      case Watch => monitor(transformed); this ! Pulse
    }
  }
}

object Monido {
  def apply(area: String, interval: Long = 100)(f: String => Unit) = {
    val monido = new Monido(area, f, interval)
    monido.start
    monido 
  }

  def kill(monido: Monido) = monido ! Die
}
