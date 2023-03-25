package com.github.d_korobkov.sd.actors
package app.model

import tethys.JsonReader

object typing {

  case class Link(value: String) extends AnyVal

  object Link {
    implicit val tethysReader: JsonReader[Link] = JsonReader.stringReader.map(Link.apply)
  }

  case class Title(value: String) extends AnyVal

  object Title {
    implicit val tethysReader: JsonReader[Title] = JsonReader.stringReader.map(Title.apply)
  }

  case class Preview(value: String) extends AnyVal

  object Preview {
    implicit val tethysReader: JsonReader[Preview] = JsonReader.stringReader.map(Preview.apply)
  }

}
