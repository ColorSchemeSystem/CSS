name := "ColorSchemeSystem"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.jsoup" % "jsoup" % "1.9.1",
  "commons-io" % "commons-io" % "2.5",
  "net.sf.flexjson" % "flexjson" % "3.3"
)

play.Project.playJavaSettings
