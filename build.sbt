name := "ColorSchemeSystem"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "net.sf.flexjson" % "flexjson" % "3.3",
  "org.jsoup" % "jsoup" % "1.9.1"
)

play.Project.playJavaSettings
