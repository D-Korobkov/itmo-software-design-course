package com.github.d_korobkov.sd.actors
package app.actor

import app.model.SearchingResults
import integration.SearchingApi

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

object MasterActor {
  sealed trait Command

  case class Ask(query: String) extends Command

  private final case class Answer(reply: SearchingResults) extends Command

  private final case object ReceiveTimeout extends Command

  def apply(replyTo: ActorRef[List[SearchingResults]], apis: List[SearchingApi[Future]], timeout: FiniteDuration): Behavior[Command] = {
    Behaviors.setup { context =>
      val answers: mutable.ListBuffer[SearchingResults] = mutable.ListBuffer.empty

      Behaviors.receiveMessage[Command] {
        case Ask(query) =>
          context.setReceiveTimeout(timeout, ReceiveTimeout)
          apis.foreach { api =>
            val replyAdapter = context.messageAdapter[SearchingResults](Answer.apply)
            val child = context.spawnAnonymous(ChildActor(api, replyAdapter))
            child.tell(ChildActor.Ask(query))
          }
          Behaviors.same

        case Answer(newAnswer) =>
          answers.addOne(newAnswer)
          if (answers.length == apis.length) {
            replyTo ! answers.toList
            Behaviors.stopped(() => println("Master actor is stopped: receive all answers"))
          } else {
            Behaviors.same
          }

        case ReceiveTimeout =>
          replyTo ! answers.toList
          Behaviors.stopped(() => println("Master actor is stopped: receive timeout"))
      }
    }
  }
}
