import sbt._
import Keys._

object Build extends sbt.Build with Version {
  val pico_kind         = "io.john-ky"  %%  "pico-kind"                 % "0.0.1-dd87cb4"
  val scalaz_core       = "org.scalaz"  %%  "scalaz-core"               % "7.2.0"

  val specs2_core       = "org.specs2"  %%  "specs2-core"               % "3.7.2"

  implicit class ProjectOps(self: Project) {
    def standard(theDescription: String) = {
      self
          .settings(scalacOptions in Test ++= Seq("-Yrangepos"))
          .settings(publishTo := Some("Releases" at "s3://dl.john-ky.io/maven/releases"))
          .settings(description := theDescription)
          .settings(isSnapshot := true)
          .settings(resolvers += Resolver.sonatypeRepo("releases"))
          .settings(addCompilerPlugin("org.spire-math" % "kind-projector" % "0.8.0" cross CrossVersion.binary))
    }

    def notPublished = self.settings(publish := {}).settings(publishArtifact := false)

    def libs(modules: ModuleID*) = self.settings(libraryDependencies ++= modules)

    def testLibs(modules: ModuleID*) = self.libs(modules.map(_ % "test"): _*)
  }
  
  lazy val `pico-fake` = Project(id = "pico-fake", base = file("pico-fake"))
      .standard("Fake project").notPublished
      .testLibs(specs2_core)

  lazy val `pico-fingertree` = Project(id = "pico-fingertree", base = file("pico-fingertree"))
      .standard("Tiny finger tree library")
      .libs(scalaz_core, pico_kind)

  lazy val root = Project(id = "all", base = file("."))
      .notPublished
      .aggregate(`pico-fingertree`, `pico-fake`)
}
