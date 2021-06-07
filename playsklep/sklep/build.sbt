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

scalaVersion := "2.12.8"


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

PlayKeys.fileWatchService := play.dev.filewatch.FileWatchService.jdk7(play.sbt.run.toLoggerProxy(sLog.value))