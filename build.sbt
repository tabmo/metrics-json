name := "metrics-json"
description := "Expose dropwizard metrics in json"
organization := "io.tabmo"

lazy val GlobalSettings = Seq(
  scalaVersion := "2.12.2",
  crossScalaVersions := Seq("2.11.8", "2.12.2"),
  resolvers ++= Seq(
    "Atlassian Releases" at "https://maven.atlassian.com/public/", // jwt
    "Scalaz bintray" at "http://dl.bintray.com/scalaz/releases",
    "Tabmo Bintray" at "https://dl.bintray.com/tabmo/maven",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.bintrayRepo("dwhjames", "maven"),
    "Bintary JCenter" at "http://jcenter.bintray.com"
  ),
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-Xlint", // Enable recommended additional warnings.
    "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
    "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
    "-Ywarn-numeric-widen", // Warn when numerics are widened.
    "-Xlint:-missing-interpolator"
  ),
  scalacOptions in(Compile, compile) ++= Seq(// Disable for tests
    "-Xlint:-missing-interpolator", // Additional warnings (see scalac -Xlint:help)
    "-Ywarn-adapted-args" // Warn if an argument list is modified to match the receive
  )
)

lazy val root = (project in file("."))
  .aggregate(`metrics-core`, `metrics-circe`,`metrics-playjson`)

lazy val `metrics-core` = (project in file("modules/metrics-core"))
  .settings(GlobalSettings)
  .settings(libraryDependencies += "nl.grons" %% "metrics-scala" % "3.5.9" % Compile)

val circeVersion = "0.8.0"
lazy val `metrics-circe` = (project in file("modules/metrics-circe"))
  .settings(GlobalSettings)
  .settings(libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion % Compile))
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test)
  .dependsOn(`metrics-core` % "compile->compile;test->compile")

val playJsonVersion = "2.6.2"
lazy val `metrics-playjson` = (project in file("modules/metrics-playjson"))
  .settings(GlobalSettings)
  .settings(libraryDependencies += "com.typesafe.play" %% "play-json" % playJsonVersion % Compile)
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test)
  .dependsOn(`metrics-core` % "compile->compile;test->compile")
