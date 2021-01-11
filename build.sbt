name := "vanguard-game"

version := "0.1"

scalaVersion := "2.13.3"
val AkkaVersion = "2.6.9"

libraryDependencies ++= List(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "org.slf4j" % "slf4j-simple" % "1.7.30",
  "org.slf4j" % "slf4j-api" % "1.7.30"
)