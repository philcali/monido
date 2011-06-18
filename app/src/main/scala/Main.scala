package com.github.philcali
package monido

import java.io.File
import java.io.{BufferedReader, InputStreamReader}

/*!# Monido

Monido comes with a command line program that spits out changes in a shell
It uses the main framework code, but can also be a stand alone program.

Install the program using n8han's conscript tool:

 * `cs philcali/monido`
 * `monido . -e ls -l`

*/
object Main {
  def printHelp {
    println("Usage: monido [-rh] [file|dir] [-e <command>]")
    def padleft(s: String) = "  %s" format(s)
    val text = List("-h         prints this help",
                    "-r         spawns recursive file monitor",
                    "file|dir   monitors this file or directory (default .)",
                    "-e         executes command with changed file (defaults prints out to terminal)")
    text.map(padleft).foreach(println)
  }

  // Parse out the terminal execution
  val Exec = """-e\s+(.+)""".r

  def doIt(args: Array[String]) {
    val help = args.contains("-h")
    val recursive = args.contains("-r")
    val idx = args.findIndexOf(_ == "-e")
    val command = args.takeRight(args.size - idx).mkString(" ") match {
      case Exec(cmd) => 
        (file: File) => {
          val rt = Runtime.getRuntime()
          // Execute the terminal command
          val pr = rt.exec("%s %s" format(cmd, file.getAbsolutePath))
          val in = new BufferedReader(new InputStreamReader(pr.getInputStream))
          def read(in: BufferedReader): Unit = in.readLine match {
            case line: String => println(line); read(in)
            case _ => 
          }
          read(in)
        }
      case _ => (file: File) => println(file)
    }
    val dir = args.find(!_.startsWith("-")) match {
      case Some(a) => 
        val file = new File(a)
        if(file.exists) file else new File(".")
      case None => new File(".")
    }


    if(help) { 
      printHelp
    } else {
      // Standard `FileMonido` behavior  
      val monitor = FileMonido(dir.getAbsolutePath, recurse=recursive)(command)
      println("Press Enter to quit")
      Console.readLine
      monitor.kill
    }
  }

  def main(args: Array[String]) {
    doIt(args)
  }
}

// Conscripted execuation
class Main extends xsbti.AppMain {
  class Exit(val code: Int) extends xsbti.Exit 
  
  def run(config: xsbti.AppConfiguration) = {
    Main.doIt(config.arguments)
    new Exit(0) 
  }
}

