package com.github.philcali
package monido

/**
 * Monido comes with a command line program that spits out changes in a shell
 * It uses the main framework code, but can also be a stand alone program.
 */
object Main {
  def main(args: Array[String]) {
    val recursive = args.size > 0 && args(0) == "-r" 
    val monitor = FileMonido(".", recurse=recursive)(println) 

    println("Press any key to quit")
    Console.readLine
  }
}

