inThisBuild(
  Seq(
    organization := "net.pikot",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.12",
    scalacOptions ++= Seq("-Xsource:3", "-Xmigration:2.13")
  )
)

// ref https://github.com/orgs/playframework/discussions/11222
val jacksonVersion = "2.15.2"
val jacksonDatabindVersion = "2.15.2"

val jacksonOverrides = Seq(
  "com.fasterxml.jackson.core" % "jackson-core",
  "com.fasterxml.jackson.core" % "jackson-annotations",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310"
).map(_ % jacksonVersion)

val jacksonDatabindOverrides = Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonDatabindVersion
)

val akkaSerializationJacksonOverrides = Seq(
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor",
  "com.fasterxml.jackson.module" % "jackson-module-parameter-names",
  "com.fasterxml.jackson.module" %% "jackson-module-scala"
).map(_ % jacksonVersion)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "gitlab-slack-forwarder",
    libraryDependencies ++= Seq(
      guice,
      ws,
      "com.auth0" % "java-jwt" % "4.4.0",
      "com.github.pureconfig" %% "pureconfig" % "0.17.4",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
      "com.slack.api" % "slack-api-client" % "1.32.0",
      // SLF4J 2.x can't be used with Play 2.8.x
      // https://github.com/playframework/playframework/issues/11499
      ("com.google.firebase" % "firebase-admin" % "9.2.0")
        .exclude("org.slf4j", "slf4j-api"),
      "com.google.cloud" % "google-cloud-logging-logback" % "0.120.8-alpha" % Runtime,
      "dev.zio" %% "zio" % "2.0.17"
    ) ++ jacksonDatabindOverrides ++ jacksonOverrides ++ akkaSerializationJacksonOverrides,
    dockerBaseImage := "openjdk:11.0-slim",
    dockerRepository := Some("us.gcr.io/gitlabslackforwarder")
  )
