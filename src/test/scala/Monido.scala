package com.github.philcali
package monido
package test

import org.scalatest.{FlatSpec, BeforeAndAfterAll}
import org.scalatest.matchers.ShouldMatchers

import java.io.File
import java.io.FileWriter

class MonidoSpec extends FlatSpec with ShouldMatchers with BeforeAndAfterAll {
  def writeText(file: String)(writer: FileWriter => Unit) {
    val w = new FileWriter(file)
    try {
      writer(w)
    } finally {
      w.close
    }
  }

  override def beforeAll(configMap: Map[String, Any]) {
    new File("temp").mkdir
    (1 to 3) foreach { index =>
      writeText("temp/file%d.txt" format(index))(_.write("This is file %d" format(index)))
    }
  }

  override def afterAll(configMap: Map[String, Any]) {
    (1 to 3) foreach { index => 
      new File("temp/file%d.txt" format(index)).delete
    }
    new File("temp").delete
  }

  "Monido" should "monitor the temp directory" in {
    val base = new File("temp").getAbsolutePath
    val monitor = FileMonido("temp") { path =>
      println(path)
      path.getAbsolutePath should be === "%s/file2.txt".format(base)
    }

    Thread.sleep(1000)

    writeText("temp/file2.txt") { writer =>
      writer.write("Hello Bobby")
    }
  
    // Hold this thread for a second
    def time = System.currentTimeMillis 
    val now = time
    def loopUntil(cond: => Boolean) { 
      if(!cond) loopUntil(cond)
    }

    loopUntil(time >= now + 1000)
    monitor.kill
  }
}
