libraryDependencies <++= (sbtVersion) (sv => Seq(
  "com.github.philcali" %% "sbt-cx-docco" % ("sbt" + sv + "_0.0.5"),
  "net.databinder" %% "conscript-plugin" % ("0.3.1_sbt"+sv)
))

libraryDependencies += "com.github.philcali" %% "cronish-sbt" % "0.0.2"
