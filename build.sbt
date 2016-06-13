name := "ColorSchemeSystem"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.jsoup" % "jsoup" % "1.9.1",
  "commons-io" % "commons-io" % "2.5",
  "net.sf.flexjson" % "flexjson" % "3.3",
  "commons-codec" % "commons-codec" % "1.10",
  "org.mindrot" % "jbcrypt" % "0.3m"
//  "mysql" % "mysql-connector-java" % "6.0.2" 
)

play.Project.playJavaSettings
