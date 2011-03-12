package com.github.philcali
package monido

import java.io.File

/**
 * Monido comes with a command line program that spits out changes in a shell
 * It uses the main framework code, but can also be a stand alone program.
 */
object Main {
  def main(args: Array[String]) {
    val recursive = args.contains("-r")
    val dir = args.find(_ != "-r") match {
      case Some(a) => 
        val file = new File(a)
        if(file.exists) file else new File(".")
      case None => new File(".")
    }
    
    val monitor = FileMonido(dir.getAbsolutePath, recurse=recursive)(println) 

    println("Press Enter to quit")
    Console.readLine
    monitor.kill
  }
}

