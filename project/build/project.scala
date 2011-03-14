import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) with assembly.AssemblyBuilder {
  val scalatest = "org.scalatest" % "scalatest" % "1.2"
  val launcher = "org.scala-tools.sbt" % "launcher-interface" % "0.7.4" % "provided"

  override def assemblyJarName = "monido.jar"
}
