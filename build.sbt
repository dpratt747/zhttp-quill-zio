ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.0"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    name := "zio-http-example",
    Defaults.itSettings,
    idePackagePrefix.withRank(KeyRanks.Invisible) := Some(
      "github.com.dpratt747"
    )
  )

lazy val zioHttpVersion = "2.0.0-RC11"
lazy val zioHttpTestVersion = "2.0.0-RC9"
lazy val zioVersion = "2.0.2"
lazy val zioConfigVersion = "3.0.2"
lazy val zioInteropVersion = "3.3.0"
lazy val circeVersion = "0.15.0-M1"
lazy val circeExtrasVersion = "0.14.2"
lazy val quillVersion = "4.6.0"
lazy val flywayVersion = "9.5.1"
lazy val postgresVersion = "42.5.0"
lazy val password4jVersion = "1.6.2"

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

lazy val testDependencies = Seq(
  "dev.zio" %% "zio-test" % zioVersion % "it,test",
  "dev.zio" %% "zio-test-sbt" % zioVersion % "it,test",
  "dev.zio" %% "zio-test-magnolia" % zioVersion % "it,test",
  "io.d11" %% "zhttp-test" % zioHttpTestVersion % "it,test"
)

libraryDependencies ++= Seq(
  "io.d11" %% "zhttp" % zioHttpVersion,
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  "dev.zio" %% "zio-interop-cats" % zioInteropVersion,
  "io.circe" %% "circe-jawn" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.getquill" %% "quill-zio" % quillVersion,
  "io.getquill" %% "quill-jdbc-zio" % quillVersion,
  "org.flywaydb" % "flyway-core" % flywayVersion,
  "org.postgresql" % "postgresql" % postgresVersion,
  "com.password4j" % "password4j" % password4jVersion
) ++ testDependencies
