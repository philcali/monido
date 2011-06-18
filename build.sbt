libraryDependencies ++= Seq (
  "org.scalatest" %% "scalatest" % "1.4.1" % "test",
  "org.scala-tools.sbt" % "launcher-interface" % "0.7.5" % "provided" from
  "http://databinder.net/repo/org.scala-tools.sbt/launcher-interface/0.7.5/jars/launcher-interface.jar"
)

doccoBasePath := file("src") 

doccoTitle := "Monido Monitoring Service"

scalaVersion := "2.9.0"

name := "monido"

organization := "com.github.philcali"

version := "0.0.2"
