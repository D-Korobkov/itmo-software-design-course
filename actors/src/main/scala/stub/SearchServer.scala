package com.github.d_korobkov.sd.actors
package stub

import sttp.client3.SttpBackend
import sttp.client3.testing.SttpBackendStub
import sttp.tapir._
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.stub.TapirStubInterpreter
import tethys.derivation.auto._

import scala.concurrent.Future
import scala.util.Random

case class SearchServer(fastResponseProbability: Double = 1) {
  final val Rand = new Random(0)

  def stub: SttpBackend[Future, Any] = TapirStubInterpreter(SttpBackendStub.asynchronousFuture)
    .whenServerEndpoint(searchEndpoint)
    .thenRunLogic()
    .backend()

  private def searchEndpoint =
    endpoint.get
      .in("api" / "search")
      .in(query[String]("q"))
      .in(query[Int]("offset").validate(Validator.min(0)))
      .in(query[Int]("limit").validate(Validator.min(0).and(Validator.max(50))))
      .out(jsonBody[SearchingResponseBody])
      .serverLogic[Future] {
        case (_, offset, limit) =>
          if (Rand.nextFloat() < fastResponseProbability) () else Thread.sleep(2500)

          Future.successful(
            Right(
              SearchingResponseBody(
                Range(offset, limit).map(_ => SingleAnswerView.generate(Rand)).toList
              )
            )
          )
      }
}