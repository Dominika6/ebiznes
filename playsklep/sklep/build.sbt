import play.sbt.PlayScala
import sbt.Resolver

name := """sklep"""
organization := "pl.edu.uj"

version := "1.0-SNAPSHOT"

lazy val `sklep` = (project in file(".")).enablePlugins(PlayScala)


resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"
resolvers += Resolver.jcenterRepo

scalaVersion := "2.12.13"
//scalaVersion := "2.12.8"


libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "7.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "7.0.0",
  "com.mohiva" %% "play-silhouette-testkit" % "7.0.0" % "test"
)

libraryDependencies ++= Seq( ehcache , ws , specs2 % Test , guice, "net.codingwell" %% "scala-guice" % "4.1.0" )
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "4.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0",
  "org.xerial"        %  "sqlite-jdbc" % "3.30.1"
)

//unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
//unmanagedResourceDirectories in Test += (baseDirectory.value / "target/web/public/test")

PlayKeys.fileWatchService := play.dev.filewatch.FileWatchService.jdk7(play.sbt.run.toLoggerProxy(sLog.value))

//
//// https://github.com/playframework/twirl/issues/105
//TwirlKeys.templateImports := Seq()
//
//scalacOptions ++= Seq(
//  "-deprecation", // Emit warning and location for usages of deprecated APIs.
//  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
//  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
//  //x"-Xfatal-warnings", // Fail the compilation if there are any warnings.
//  //"-Xlint", // Enable recommended additional warnings.
//  "-Ywarn-dead-code", // Warn when dead code is identified.
//  "-Ywarn-numeric-widen", // Warn when numerics are widened.
//  // Play has a lot of issues with unused imports and unsued params
//  // https://github.com/playframework/playframework/issues/6690
//  // https://github.com/playframework/twirl/issues/105
//  "-Xlint:-unused,_"
//)


//sources in doc in Compile := List()
