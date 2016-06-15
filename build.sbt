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
  "org.mindrot" % "jbcrypt" % "0.3m",
  "mysql" % "mysql-connector-java" % "5.1.39",
  "org.apache.httpcomponents" % "httpclient" % "4.5.2",
  "com.github.javafaker" % "javafaker" % "0.10"
)

play.Project.playJavaSettings
