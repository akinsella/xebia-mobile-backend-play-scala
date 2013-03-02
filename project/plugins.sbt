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

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"


// Use the Play sbt plugin for Play projects
// addSbtPlugin("play" % "sbt-plugin" % "2.0.4")
addSbtPlugin("play" % "sbt-plugin" % "2.1.0")

// addSbtPlugin("com.mojolly.scalate" % "xsbt-scalate-generator" % "0.2.0")

libraryDependencies += "com.typesafe" % "play-plugins-mailer_2.10" % "2.1.0"

// addSbtPlugin("net.litola" % "play-sass" % "0.1.3")
