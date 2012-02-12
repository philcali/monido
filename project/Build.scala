import sbt._

import Keys._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    organization := "com.github.philcali",
    scalaVersion := "2.9.1",
    crossScalaVersions := Seq("2.9.1", "2.9.0-1", "2.9.0", "2.8.1", "2.8.0"),
    version := "0.1.2",
    publishTo <<= version { v =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { x => false },
    pomExtra := (
      <url>https://github.com/philcali/monido</url>
      <licenses>
        <license>
          <name>The MIT License</name>
          <url>http://www.opensource.org/licenses/mit-license.php</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:philcali/monido.git</url>
        <connection>scm:git:git@github.com:philcali/monido.git</connection>
      </scm>
      <developers>
        <developer>
          <id>philcali</id>
          <name>Philip Cali</name>
          <url>http://philcalicode.blogspot.com/</url>
        </developer>
      </developers>
    )
  )
}

object Monido extends Build {

  lazy val core = Project (
    "monido-core",
    file("core"),
    settings = General.settings ++ Seq (
      libraryDependencies <+= (scalaVersion) {
        case v if v contains "2.8" => 
          "org.scalatest" % "scalatest" % "1.3" % "test"
        case _ =>
          "org.scalatest" %% "scalatest" % "1.6.1" % "test"
      }
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
