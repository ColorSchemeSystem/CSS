name := "ColorSchemeSystem"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.jsoup" % "jsoup" % "1.9.1",
  "commons-io" % "commons-io" % "2.5"
)

play.Project.playJavaSettings
