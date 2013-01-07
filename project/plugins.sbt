// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
//resolvers += Resolver.url("sbt-plugin-releases",
//  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(
//    Resolver.ivyStylePatterns)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0.4")

// addSbtPlugin("com.mojolly.scalate" % "xsbt-scalate-generator" % "0.2.0")


addSbtPlugin("net.litola" % "play-sass" % "0.1.2" from "http://cloud.github.com/downloads/jlitola/play-sass/play-sass-0.1.2.jar")