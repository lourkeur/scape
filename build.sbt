val catsVersion = "2.2.0-M3"
val dottyVersion = "0.24.0"
val scalaTestkitVersion = "2.13.2"
val junitVersion = "4.12"
val junitInterfaceVersion = "0.11"
val scalacheckVersion = "1.14.3"

lazy val common = Seq(
  scalaVersion := dottyVersion,
  scalacOptions ++= Seq(
    "-deprecation",
    "-Yexplicit-nulls",
  ),
)

lazy val root = project
  .in(file("."))
  .settings(common)
  .settings(
    name := "scape",
    version := "0.1.0",
  )

lazy val tests = project
  .dependsOn(root, cats)
  .settings(common)
  .settings(
    libraryDependencies ++= Seq(
      ("org.scalacheck" %% "scalacheck" % scalacheckVersion).withDottyCompat(dottyVersion),
      ("org.typelevel" %% "cats-laws" % catsVersion).withDottyCompat(dottyVersion),
      "org.scala-lang" % "scala-testkit" % scalaTestkitVersion % Test,
      "junit" % "junit" % junitVersion % Test,
      "com.novocode" % "junit-interface" % junitInterfaceVersion % Test,
    ),
  )

lazy val cats = project
  .settings(common)
  .dependsOn(root)
  .settings(
    libraryDependencies ++= Seq(
      ("org.typelevel" %% "cats-core" % catsVersion).withDottyCompat(dottyVersion),
    ),
  )
