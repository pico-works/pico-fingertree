import sbt._
import Keys._

object Build extends sbt.Build with Version {
  implicit class ProjectOps(self: Project) {
    def standard: Project = {
      self
          .settings(organization := "io.john-ky")
          .settings(scalacOptions := Seq(
              "-feature",
              "-deprecation",
              "-unchecked",
              "-Xlint",
              "-Yrangepos",
              "-encoding",
              "utf8"))
          .settings(scalacOptions in (console) += "-Yrangepos")
    }

    def notPublished: Project = {
      self
          .settings(publish := {})
          .settings(publishArtifact := false)
    }

    def published: Project = {
      self
          .settings(publishTo := Some("Scalap Releases" at "s3://dl.john-ky.io/maven/releases"))
          .settings(isSnapshot := true)
    }
  }
  
  lazy val `pico-fake` = Project(id = "pico-fake", base = file("pico-fake"))
      .standard("Fake project").notPublished
      .testLibs(specs2_core)

  lazy val `pico-fingertree` = Project(id = "pico-fingertree", base = file("pico-fingertree"))
    .standard
    .published
    .settings(libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % "7.1.2",
      "io.john-ky" %% "pico-kind"   % "0.0.1-dd87cb4"))

  lazy val root = Project(id = "all", base = file("."))
    .notPublished
    .aggregate(`pico-fingertree`, `pico-fake`)
}
