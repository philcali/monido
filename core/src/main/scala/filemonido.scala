package com.github.philcali
package monido

import java.io.File

/*!# FileMonido

A `FileMonido` is a mondio reference implementation that monitors a file or
folder on the filesystem. It can be initiated recursivley, but this can be a
dangerous prospect, as each monido consumes two threads.
*/
trait FileMonitorImpl extends MonitorComponent {
  this: ListeningComponent[File] =>
  class FileMonitor(area: String) extends MonitorDevice {
    // Require the existence of the file or filder we are monitoring.
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
    // Using `Actor` loop for storing *old* files
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

object FileMonido extends MonidoFactory[File, File] {
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

  def create(file: File, interval: Long, handler: File => Unit) =
    new Monido with ListeningComponent[File] 
               with FileMonitorImpl 
               with PulsingComponentImpl {
      val listener = new MonidoListener[File] {
        def changed(item: File) = handler(item)
      }
      val monitor = new FileMonitor(file.getAbsolutePath)
      val pulsar = new Pulsar(interval)
    }
}

