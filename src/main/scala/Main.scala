package com.github.philcali
package monido

import java.io.File

/**
 * Monido comes with a command line program that spits out changes in a shell
 * It uses the main framework code, but can also be a stand alone program.
 */
object Main {
  def printHelp {
    println("monido [-rh] [file|dir]")
    def padleft(s: String) = "  %s" format(s)
    val text = List("-h         prints this help",
                    "-r         spawns recursive file monitor",
                    "file|dir   monitors this file or directory (default .)")
    text.map(padleft).foreach(println)
  }

  def doIt(args: Array[String]) {
    val help = args.contains("-h")
    val recursive = args.contains("-r")
    val dir = args.find(!_.startsWith("-")) match {
      case Some(a) => 
        val file = new File(a)
        if(file.exists) file else new File(".")
      case None => new File(".")
    }

    if(help) { 
      printHelp
    } else { 
      val monitor = FileMonido(dir.getAbsolutePath, recurse=recursive)(println)
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

