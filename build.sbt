inThisBuild(
  Seq(
    organization := "net.pikot",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.2"
  )
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "gitlab-slack-forwarder",
    libraryDependencies ++= Seq(
      guice,
      ws,
      "com.github.pureconfig" %% "pureconfig" % "0.12.3",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "com.slack.api" % "slack-api-client" % "1.0.11",
      "com.google.firebase" % "firebase-admin" % "7.0.0"
    ),
    dockerBaseImage := "openjdk:11.0-slim",
    dockerRepository := Some("us.gcr.io/gitlabslackforwarder")
  )
