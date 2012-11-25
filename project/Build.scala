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
      "mysql" % "mysql-connector-java" % "5.1.21",
      "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
      "org.fusesource.scalate" % "scalate-core" % "1.5.3",
      "net.debasishg" % "redisclient_2.9.2" % "2.7"/*,
      "eu.teamon" %% "play-navigator" % "0.4.0"*/
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
      // Add extra resolver for the twitter
      resolvers += "Twitter repo" at "http://maven.twttr.com/" ,
      resolvers += "DevJava repo" at "http://download.java.net/maven/2/"/*,
      resolvers += "scalajars.org repo" at "http://scalajars.org/repository"*/
    )

}
