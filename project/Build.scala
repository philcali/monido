import sbt._

import Keys._

/** Cronish stuff */
import cronish.dsl._
import CronishPlugin._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    organization := "com.github.philcali",
    scalaVersion := "2.9.1",
    crossScalaVersions := Seq("2.9.1", "2.9.0-1", "2.9.0", "2.8.1", "2.8.0"),
    version := "0.1.1",
    publishTo := Some("Scala Tools Nexus" at 
                      "http://nexus.scala-tools.org/content/repositories/releases/"),
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
  )
}

object Monido extends Build {

  lazy val specialSettings: Seq[Setting[_]] =
    General.settings ++ CronishPlugin.cronishSettings ++ Seq (
      cronish.tasks := Seq (
        add sh "echo Take a break" runs hourly 
      )
    )

  lazy val core = Project (
    "monido-core",
    file("core"),
    settings = General.settings ++ Seq (
      libraryDependencies <+= (scalaVersion) {
        case v if v contains "2.8" => 
          "org.scalatest" % "scalatest" % "1.3" % "test"
        case _ =>
          "org.scalatest" %% "scalatest" % "1.6.1" % "test"
      },
      publishArtifact in (Compile, packageSrc) := false,
      publishArtifact in (Compile, packageDoc) := false
    )
  )

  lazy val app = Project (
    "monido-app",
    file("app"),
    settings = General.settings ++ Seq (
      libraryDependencies <+= (sbtVersion) { 
        "org.scala-tools.sbt" %% "launcher-interface" % _ % "provided"
      }
    )
  ) dependsOn (core)
}
