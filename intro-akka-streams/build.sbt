import Dependencies._

lazy val root = (project in file(".")).
  enablePlugins(JavaAppPackaging).
  settings(
    inThisBuild(List(
      organization := "me.andrusha",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Intro Akka Streams",
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    javacOptions ++= Seq("-source", "10", "-target", "10"),
    libraryDependencies ++= testingDeps.map(_ % Test),
    libraryDependencies ++= streaming,
    libraryDependencies ++= jsonParsing,
    // Used in circe to derive encoders
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )
