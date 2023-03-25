package com.github.d_korobkov.sd.actors
package app.actor

import app.model.{SearchingResults, SingleAnswer}
import integration.SearchingApi

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.Future
import scala.util._

object ChildActor {
  sealed trait Command

  final case class Ask(query: String) extends Command

  private final case class Answer(searchingResults: SearchingResults, replyTo: ActorRef[SearchingResults]) extends Command

  def apply(api: SearchingApi[Future], replyTo: ActorRef[SearchingResults]): Behavior[Command] = {
    Behaviors.receive[Command] { (context, command) =>
      command match {
        case Ask(query) =>
          val topFiveAnswers = api.search(query, 0, 5)
          context.pipeToSelf(topFiveAnswers) {
            case Success(response) =>
              val results = response.searchingResults.map(view => SingleAnswer(view.link, view.title, view.preview))
              Answer(SearchingResults.Success(api.source, results), replyTo)
            case Failure(th) =>
              Answer(SearchingResults.Failed(api.source, th.getMessage), replyTo)
          }
          Behaviors.same

        case Answer(result, replyTo) =>
          replyTo.tell(result)
          Behaviors.stopped(() => println(s"${api.source}: child actor is stopped"))
      }
    }
  }
}
