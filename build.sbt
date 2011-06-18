libraryDependencies <++= (scalaVersion) { v => 
  Seq (
    v match {
      case v if v contains "2.8" => 
        "org.scalatest" % "scalatest" % "1.3" % "test"
      case _ =>
        "org.scalatest" %% "scalatest" % "1.4.1" % "test"
    },
    "org.scala-tools.sbt" % "launcher-interface" % "0.7.5" % "provided" from
    "http://databinder.net/repo/org.scala-tools.sbt/launcher-interface/0.7.5/jars/launcher-interface.jar"
  )
}

doccoTitle := "Monido Monitoring Service"

crossScalaVersions := Seq("2.9.0", "2.8.1")

scalaVersion := "2.9.0"

name := "monido"

organization := "com.github.philcali"

version := "0.0.2"
