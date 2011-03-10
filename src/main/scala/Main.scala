package com.github.philcali
package monido

/**
 * Monido comes with a command line program that spits out changes in a shell
 * It uses the main framework code, but can also be a stand alone program.
 */
object Main {
  def main(args: Array[String]) {
    val monitor = Monido(".")(println) 

    // Never ending
    val input = Console.readChar
    if(input == 'q') {
      Monido.kill(monitor)
      println("Terminating")
    }
  }
}

