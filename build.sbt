ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "zio-http-example",
    idePackagePrefix.withRank(KeyRanks.Invisible) := Some("github.com.dpratt747")
  )

lazy val zioHttpVersion = "2.0.0-RC11"
lazy val zioHttpTestVersion = "2.0.0-RC9"
lazy val zioVersion = "2.0.2"
lazy val zioConfigVersion = "3.0.2"
lazy val circeVersion = "0.15.0-M1"
lazy val circeExtrasVersion = "0.14.2"

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

lazy val testDependencies = Seq(
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
  "io.d11" %% "zhttp-test" % zioHttpTestVersion % Test
)

libraryDependencies ++= Seq(
  "io.d11" %% "zhttp" % zioHttpVersion,
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  "io.circe" %% "circe-jawn" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
) ++ testDependencies