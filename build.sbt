scalaVersion in ThisBuild := "2.10.5"

crossScalaVersions := Seq("2.10.5", "2.11.6")

resolvers in ThisBuild ++= Seq(
    "bintray/non"     at "http://dl.bintray.com/non/maven",
    "dl-john-ky"      at "http://s3-ap-southeast-2.amazonaws.com/dl.john-ky.io/maven/releases")

version in ThisBuild := buildVersion
