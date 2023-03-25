package com.github.d_korobkov.sd.actors

import app.actor.MasterActor
import app.model.SearchingResults
import integration.impl._
import stub.SearchServer

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object Main {

  object App {
    def apply(): Behavior[List[SearchingResults]] =
      Behaviors.receiveMessage { aggregatedSearchingResults =>
        Thread.sleep(1000)
        aggregatedSearchingResults.foreach(results => print(results.show))
        Behaviors.stopped
      }
  }

  def main(args: Array[String]): Unit = {
    val system = ActorSystem(App(), "app")
    implicit val ec: ExecutionContext = system.executionContext

    val slowServer = SearchServer(fastResponseProbability = 0)
    val fastServer = SearchServer()

    val bing = BingApi(fastServer)
    val google = GoogleApi(slowServer)
    val yandex = YandexApi(fastServer)

    val printer = system.ref
    val masterActor = system.systemActorOf(MasterActor(printer, List(bing, google, yandex), 2.seconds), "master")

    masterActor.tell(MasterActor.Ask("how much"))

    Thread.sleep(5000)
  }
}