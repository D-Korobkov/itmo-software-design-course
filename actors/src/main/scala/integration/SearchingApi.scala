package com.github.d_korobkov.sd.actors
package integration

import app.model.Source

trait SearchingApi[F[_]] {

  val source: Source

  def search(query: String, offset: Int, limit: Int): F[SearchingResponseBody]

}
