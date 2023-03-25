ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.6"

lazy val root = (project in file("."))
  .settings(
    name := "actors",
    idePackagePrefix := Some("com.github.d_korobkov.sd.actors")
  )
  .settings(
    libraryDependencies ++= Seq(
      Dependency.AkkaActor,
      Dependency.Tapir,
      Dependency.TapirJsonTethys,
      Dependency.JsonTethysDerivation,
      Dependency.AkkaActorTestkit % Test,
      Dependency.Scalatest % Test,
      Dependency.ScalatestPlusMock % Test,
    )
  )
