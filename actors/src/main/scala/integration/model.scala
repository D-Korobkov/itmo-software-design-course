package com.github.d_korobkov.sd.actors
package integration

import app.model.typing._

import tethys.JsonReader
import tethys.derivation.semiauto.jsonReader

final case class SearchingResponseBody(searchingResults: List[SingleAnswerView])

object SearchingResponseBody {
  implicit val tethysReader: JsonReader[SearchingResponseBody] = jsonReader
}

final case class SingleAnswerView(link: Link, title: Title, preview: Preview)

object SingleAnswerView {
  implicit val tethysReader: JsonReader[SingleAnswerView] = jsonReader
}
