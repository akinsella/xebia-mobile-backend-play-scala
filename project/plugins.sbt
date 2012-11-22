// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
//resolvers += Resolver.url("sbt-plugin-releases",
//  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(
//    Resolver.ivyStylePatterns)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0.3")

addSbtPlugin("com.mojolly.scalate" % "xsbt-scalate-generator" % "0.2.0")

