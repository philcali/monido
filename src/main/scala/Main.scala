package com.github.philcali
package monido

import java.io.File
import java.io.{BufferedReader, InputStreamReader}

/**
 * Monido comes with a command line program that spits out changes in a shell
 * It uses the main framework code, but can also be a stand alone program.
 */
object Main {
  def printHelp {
    println("monido [-rh] [file|dir] [-e=<command>]")
    def padleft(s: String) = "  %s" format(s)
    val text = List("-h         prints this help",
                    "-r         spawns recursive file monitor",
                    "file|dir   monitors this file or directory (default .)",
                    "-e         executes command with changed file (defaults prints out to terminal)")
    text.map(padleft).foreach(println)
  }

  val Exec = """-e=(.+)""".r

  def doIt(args: Array[String]) {
    val help = args.contains("-h")
    val recursive = args.contains("-r")
    val command = args.find(_.startsWith("-e")) match {
      case Some(Exec(cmd)) => 
        (file: File) => {
          val rt = Runtime.getRuntime()
          val pr = rt.exec("%s %s" format(cmd, file.getAbsolutePath))
          val in = new BufferedReader(new InputStreamReader(pr.getInputStream))
          def read(in: BufferedReader): Unit = in.readLine match {
            case line: String => println(line); read(in)
            case _ => 
          }
          read(in)
        }
      case None => (file: File) => println(file)
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

class Main extends xsbti.AppMain {
  class Exit(val code: Int) extends xsbti.Exit 
  
  def run(config: xsbti.AppConfiguration) = {
    Main.doIt(config.arguments)
    new Exit(0) 
  }
}

