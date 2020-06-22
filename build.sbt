name := """gitlab-slack-forwarder"""
organization := "net.pikot"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.2"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.12.3"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "com.slack.api" % "slack-api-client" % "1.0.10"
libraryDependencies += "com.google.firebase" % "firebase-admin" % "6.11.0"
//libraryDependencies += "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.1"

dockerBaseImage := "openjdk:11.0-slim"
dockerRepository := Some("us.gcr.io/gitlabslackforwarder")

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "net.pikot.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "net.pikot.binders._"
