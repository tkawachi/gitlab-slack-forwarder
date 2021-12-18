inThisBuild(
  Seq(
    organization := "net.pikot",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.7"
  )
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "gitlab-slack-forwarder",
    libraryDependencies ++= Seq(
      guice,
      ws,
      "com.github.pureconfig" %% "pureconfig" % "0.17.1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
      "com.slack.api" % "slack-api-client" % "1.16.0",
      "com.google.firebase" % "firebase-admin" % "8.1.0",
      "com.google.cloud" % "google-cloud-logging-logback" % "0.120.8-alpha" % Runtime
    ),
    dockerBaseImage := "openjdk:11.0-slim",
    dockerRepository := Some("us.gcr.io/gitlabslackforwarder")
  )
