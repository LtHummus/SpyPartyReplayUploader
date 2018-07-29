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
  "org.slf4j" % "slf4j-simple" % "1.6.4"
)

