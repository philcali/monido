import sbt._

import Keys._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    organization := "com.github.philcali",
    scalaVersion := "2.9.0",
    crossScalaVersions := Seq("2.9.0-1", "2.9.0", "2.8.1", "2.8.0"),
    version := "0.0.2"
  )
}

object Monido extends Build {
  lazy val monido = Project (
    "monido",
    file("."),
    settings = General.settings ++ Seq (
      doccoTitle := "Monido Monitoring Service"
    )
  ) aggregates (core, app)

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
        "org.scala-tools.sbt" % "launcher-interface" % "0.7.5" % "provided" from
        "http://databinder.net/repo/org.scala-tools.sbt/launcher-interface/0.7.5/jars/launcher-interface.jar"
    )
  )
}