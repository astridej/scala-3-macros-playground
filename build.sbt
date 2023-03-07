ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "playground-macros"
  )

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest"  %% "scalatest"      % "3.2.15" % Test
libraryDependencies += "org.scala-lang" %% "scala3-staging" % scalaVersion.value
// https://mvnrepository.com/artifact/org.scalatest/scalatest-shouldmatchers
libraryDependencies += "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.15" % Test
