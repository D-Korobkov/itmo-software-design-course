import sbt._

object Dependency {

  private object version {
    final val Scalatest = "3.2.15"
    final val ScalatestPlusMock = "3.2.15.0"
    final val AkkaCore = "2.7.0"
    final val Tapir = "1.2.9"
    final val TethysJson = "0.26.0"
  }

  final val Scalatest = "org.scalatest" %% "scalatest" % version.Scalatest
  final val ScalatestPlusMock = "org.scalatestplus" %% "mockito-4-6" % version.ScalatestPlusMock

  final val AkkaActor = "com.typesafe.akka" %% "akka-actor-typed" % version.AkkaCore
  final val AkkaActorTestkit = "com.typesafe.akka" %% "akka-actor-testkit-typed" % version.AkkaCore

  final val JsonTethysDerivation = "com.tethys-json" %% "tethys-derivation" % version.TethysJson

  final val Tapir = "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server" % version.Tapir
  final val TapirJsonTethys = "com.softwaremill.sttp.tapir" %% "tapir-json-tethys" % version.Tapir
}
