import sbt._
import Keys._

object Multibuild extends Build with Version {
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
          .settings(publishTo in ThisBuild := Some("Scalap Releases" at "s3://dl.john-ky.io/maven/snapshots"))
          .settings(isSnapshot in ThisBuild  := true)
    }

    def dependsOnAndAggregates(projects: Project*): Project = {
      val dependencies = projects.map(pr => pr: sbt.ClasspathDep[sbt.ProjectReference])
      val aggregates = projects.map(pr => pr: sbt.ProjectReference)
      self.dependsOn(dependencies: _*).aggregate(aggregates: _*)
    }
  }
  
  lazy val `pico-fingertree` = Project(id = "pico-fingertree", base = file("pico-fingertree"))
    .standard
    .published
    .settings(libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % "7.1.2",
      "io.john-ky" %% "pico-kind"   % "0.0.1-66ce0d8"))
    

  lazy val root = Project(id = "all", base = file("."))
    .notPublished
    .aggregate(`pico-fingertree`)
}
