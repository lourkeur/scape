val dottyVersion = "0.24.0-RC1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "unboxed-try",
    version := "0.1.0",

    scalaVersion := dottyVersion,
  )
