import sbt._

object Dependencies {
  val akkaVersion = "2.5.17"
  val akkaHttpVersion = "10.1.5"
  val circeVersion = "0.10.0"

  lazy val testingDeps = Seq(
    "org.scalatest" %% "scalatest" % "3.0.5",
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion
  )

  lazy val streaming = Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  )

  lazy val jsonParsing = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-optics"
  ).map(_ % circeVersion)
}
