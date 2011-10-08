package monido

import java.io.File

/*!# FileMonido

A `FileMonido` is a mondio reference implementation that monitors a file or
folder on the filesystem. It can be initiated recursivley, but this can be a
dangerous prospect, as each monido consumes two threads.
*/
trait FileMonitorImpl extends MonitorComponent {
  this: ListeningComponent[FileSignal] =>
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
    def transform = initiate.map(file => (file.getAbsolutePath, file.lastModified))
    // Using `Actor` loop for storing *old* files
    def body {
      val old = transform
      react {
        case Pulse => 
          val current = initiate
          current.foreach { file =>
            old.find(_._1 == file.getAbsolutePath) match {
              case Some(found) => 
                if(found._2 != file.lastModified) listener.changed(Modified(file))
              case None =>
                listener.changed(Created(file))
            }
          }
          // Find deleted ones
          old.foreach { case (path, mod) =>
            current.find(_.getAbsolutePath == path) match {
              case Some(f) =>
              case None => listener.changed(Deleted(path))
            }
          }
        case Die => this.exit
      }
    }
  }
}

sealed trait FileSignal
case class Created(file: File) extends FileSignal
case class Modified(file: File) extends FileSignal
case class Deleted(file: String) extends FileSignal

// Helpful Extractor
object ModifiedOrCreated {
  def unapply(sig: FileSignal) = sig match {
    case Modified(file) => Some(file)
    case Created(file) => Some(file)
    case _ => None
  }
}

object FileMonido extends MonidoFactory[File, FileSignal] {
  private def recuresively(dir: File)(body: File => Monido): Unit = {
    val allFiles = dir.listFiles.filter(file => 
                                       !file.getName.startsWith(".") && 
                                        file.isDirectory)
    allFiles.foreach { file => 
      body(file)
      recuresively(file)(body)
    }
  }

  def apply(area: String, interval: Long = 500, recurse: Boolean = false) 
           (handler: FileSignal => Unit): Monido = {
    val file = new File(area)
    if(recurse && file.isDirectory) {
      recuresively(file) { file =>
        apply(file, interval)(handler)
      }
    }
    apply(file, interval)(handler)
  }

  def create(file: File, interval: Long, handler: FileSignal => Unit) =
    new Monido with ListeningComponent[FileSignal] 
               with FileMonitorImpl 
               with PulsingComponentImpl {
      val listener = new MonidoListener[FileSignal] {
        def changed(item: FileSignal) = handler(item)
      }
      val monitor = new FileMonitor(file.getAbsolutePath)
      val pulsar = new Pulsar(interval)
    }
}

