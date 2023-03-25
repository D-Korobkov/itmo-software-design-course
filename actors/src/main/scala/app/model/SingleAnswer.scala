package com.github.d_korobkov.sd.actors
package app.model

import app.model.typing.{Link, Preview, Title}

final case class SingleAnswer(link: Link, title: Title, preview: Preview) {
  def show: String =
    s"""
       |${link.value}
       |${title.value.capitalize}
       |${preview.value}
       |""".stripMargin
}
