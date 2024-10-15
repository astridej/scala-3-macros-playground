ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

val http4sVersion = "1.0.0-M42"

lazy val root = (project in file("."))
  .settings(
    name := "playground-macros"
  )

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test
//libraryDependencies += "org.scala-lang" %% "scala3-staging" % scalaVersion.value
// https://mvnrepository.com/artifact/org.scalatest/scalatest-shouldmatchers
libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.19" % Test
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-circe"        % http4sVersion
)
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.4"
// https://mvnrepository.com/artifact/io.circe/circe-core
libraryDependencies += "io.circe"      %% "circe-core"    % "0.15.0-M1"
libraryDependencies += "io.circe"      %% "circe-generic" % "0.15.0-M1"
libraryDependencies += "io.circe"      %% "circe-parser"  % "0.15.0-M1"
libraryDependencies += "org.typelevel" %% "literally"     % "1.2.0"
