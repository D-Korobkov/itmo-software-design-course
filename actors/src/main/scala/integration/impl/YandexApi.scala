package com.github.d_korobkov.sd.actors
package integration.impl

import app.model.Source
import integration.{SearchingApi, SearchingResponseBody}
import stub.SearchServer

import sttp.client3.UriContext
import tethys._
import tethys.jackson._

import scala.concurrent.{ExecutionContext, Future}

case class YandexApi(server: SearchServer)(implicit ec: ExecutionContext) extends SearchingApi[Future] {

  override val source: Source = Source.Yandex

  override def search(query: String, offset: Int, limit: Int): Future[SearchingResponseBody] =
    sttp.client3.basicRequest
      .get(uri"https://www.ya.ru/api/search?q=$query&offset=$offset&limit=$limit")
      .send(server.stub)
      .map(_.body)
      .flatMap {
        case Left(errorResponse) => Future.failed(new RuntimeException(errorResponse))
        case Right(successResponse) => successResponse.jsonAs[SearchingResponseBody].fold(Future.failed, Future.successful)
      }

}
