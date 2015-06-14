import sbt._
import Keys._

object JohnsFingerTree extends Build {
  lazy val buildSettings = Seq(
      organization := "io.john-ky",
      scalacOptions := Seq(
        "-feature",
        "-deprecation",
        "-unchecked",
        "-Xlint",
        "-Yrangepos",
        "-encoding",
        "utf8"),
      scalacOptions in (console) += "-Yrangepos"
  )

  lazy val commonSettings = Defaults.defaultSettings ++ buildSettings

  lazy val cj = Project(id = "cj", base = file("cj"))
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.2")

  lazy val root = Project(id = "livefx", base = file("."))
    .aggregate(cj)
    .settings(commonSettings: _*)
}

