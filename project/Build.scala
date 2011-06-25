import sbt._

import Keys._
import CxDocco._
import SbtCronish._

import com.github.philcali.cronish.dsl._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    organization := "com.github.philcali",
    scalaVersion := "2.9.0",
    crossScalaVersions := Seq("2.9.0", "2.8.1", "2.8.0"),
    version := "0.0.3",
    publishTo := Some("Scala Tools Nexus" at 
                      "http://nexus.scala-tools.org/content/repositories/releases/"),
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
  )
}

object Monido extends Build {
  lazy val monido = Project (
    "monido",
    file("."),
    settings = General.settings ++ doccoSettings ++ cronishSettings ++ Seq (
      doccoTitle := "Monido Monitoring Service",
      cronishTasks := Seq (
        add sh "echo Take a break" runs hourly
      )
    )
  ) aggregate (app, core)

  lazy val core = Project (
    "monido-core",
    file("core"),
    settings = General.settings ++ Seq (
      libraryDependencies <+= (scalaVersion) {
        case v if v contains "2.8" => 
          "org.scalatest" % "scalatest" % "1.3" % "test"
        case _ =>
          "org.scalatest" %% "scalatest" % "1.4.1" % "test"
      }
    )
  )

  lazy val app = Project (
    "monido-app",
    file("app"),
    settings = General.settings ++ Seq (
      libraryDependencies +=
        "org.scala-tools.sbt" % "launcher-interface" % "0.10.0" 
    )
  ) dependsOn (core)
}
