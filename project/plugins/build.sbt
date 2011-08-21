libraryDependencies <+= (sbtVersion) (sv => 
  "com.github.philcali" %% "sbt-cx-docco" % ("sbt" + sv + "_0.0.5")
)

libraryDependencies += "com.github.philcali" %% "cronish-sbt" % "0.0.3"
