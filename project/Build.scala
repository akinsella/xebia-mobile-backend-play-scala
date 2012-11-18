import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "xebia-mobile-backend-play-scala"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "com.notnoop.apns" % "apns" % "0.1.6",
      "com.twitter" % "querulous" % "2.6.5",
      "mysql" % "mysql-connector-java" % "5.1.21"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here
      // Add extra resolver for the twitter
      resolvers += "Twitter repo" at "http://maven.twttr.com/" ,
      resolvers += "DevJava repo" at "http://download.java.net/maven/2/"
    )

}
