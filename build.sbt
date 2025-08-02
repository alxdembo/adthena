// build.sbt
ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "shopping-basket",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    ),

    // Main class for running the application
    Compile / mainClass := Some("com.shoppingbasket.PriceBasket"),

    // Assembly plugin settings for creating fat JAR
    assembly / mainClass := Some("com.shoppingbasket.PriceBasket"),
    assembly / assemblyJarName := "price-basket.jar",

    // Test settings
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD"),

    // Compiler options
    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint"
    )
  )
