import sbt.Keys._
import sbt._

object Build extends sbt.Build {
  val pico_fp                   = "org.pico"        %%  "pico-fp"                   % "0.0.3-26-29672cd"

  val kind_projector            = "org.spire-math"  %   "kind-projector"            % "0.8.0"

  val specs2_core               = "org.specs2"      %%  "specs2-core"               % "3.7.2"

  implicit class ProjectOps(self: Project) {
    def standard(theDescription: String) = {
      self
          .settings(scalacOptions in Test ++= Seq("-Yrangepos"))
          .settings(publishTo := Some("Releases" at "s3://dl.john-ky.io/maven/releases"))
          .settings(description := theDescription)
          .settings(isSnapshot := true)
          .settings(addCompilerPlugin(kind_projector cross CrossVersion.binary))
    }

    def notPublished = self.settings(publish := {}).settings(publishArtifact := false)

    def libs(modules: ModuleID*) = self.settings(libraryDependencies ++= modules)

    def testLibs(modules: ModuleID*) = self.libs(modules.map(_ % "test"): _*)
  }

  lazy val `pico-fake` = Project(id = "pico-fake", base = file("pico-fake"))
      .standard("Fake project").notPublished
      .testLibs(specs2_core)

  lazy val `pico-fingertree` = Project(id = "pico-fingertree", base = file("pico-fingertree"))
      .standard("Finger Tree library")
      .libs(pico_fp)
      .testLibs(specs2_core)

  lazy val all = Project(id = "pico-fingertree-project", base = file("."))
      .notPublished
      .aggregate(`pico-fingertree`, `pico-fake`)
}
