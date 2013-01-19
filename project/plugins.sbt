// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
//resolvers += Resolver.url("sbt-plugin-releases",
//  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(
//    Resolver.ivyStylePatterns)

// scalaVersion := "2.10.0"


resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// The Typesafe snapshots repository
resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/simple/maven-snapshots/"

resolvers += "Patience Releases" at "http://repo.patience.io/"

// Use the Play sbt plugin for Play projects
// addSbtPlugin("play" % "sbt-plugin" % "2.0.4")
addSbtPlugin("play" % "sbt-plugin" % "2.1-RC2")

// addSbtPlugin("com.mojolly.scalate" % "xsbt-scalate-generator" % "0.2.0")

libraryDependencies += "com.typesafe" % "play-plugins-mailer_2.10" % "2.1-SNAPSHOT"

// addSbtPlugin()


//addSbtPlugin("net.litola" % "play-sass" % "0.1.2" from "http://cloud.github.com/downloads/jlitola/play-sass/play-sass-0.1.2.jar")

addSbtPlugin("net.litola" % "play-sass" % "0.1.3" from "https://raw.github.com/tthraine/play-sass/master/play-sass-0.1.3.jar")