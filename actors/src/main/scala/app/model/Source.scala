package com.github.d_korobkov.sd.actors
package app.model

sealed trait Source

object Source {
  case object Bing extends Source

  case object Google extends Source

  case object Yandex extends Source
}
