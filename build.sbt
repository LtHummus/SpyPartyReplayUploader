name := """SpyPartyReplayUploader"""
organization := "com.lthummus"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.lthummus.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.lthummus.binders._"

libraryDependencies ++= Seq(
  //database stuff
  "mysql" % "mysql-connector-java" % "8.0.11",
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.typesafe.slick" %% "slick-codegen" % "3.2.3",


  //logging
  "org.slf4j" % "slf4j-simple" % "1.6.4",

  //cache
  "com.github.cb372" %% "scalacache-core" % "0.24.2",
  "com.github.cb372" %% "scalacache-caffeine" % "0.24.2",

  //functional programming
  "org.scalaz" %% "scalaz-core" % "7.2.25",

  //S3
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.375",

  //serializing sealed traits
  "io.leonard" %% "play-json-traits" % "1.4.4",

  //long live apache commons
  "commons-io" % "commons-io" % "2.6"

)

