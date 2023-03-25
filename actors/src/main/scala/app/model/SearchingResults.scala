package com.github.d_korobkov.sd.actors
package app.model

sealed trait SearchingResults {
  def show: String
}

object SearchingResults {

  final case class Success(source: Source, answers: List[SingleAnswer]) extends SearchingResults {
    override def show: String =
      s"""
         |> $source:
         |${answers.map(_.show).mkString.indent(2)}
         |""".stripMargin
  }

  final case class Failed(source: Source, message: String) extends SearchingResults {
    override def show: String =
      s"""
         |$source
         |  ERROR: $message
         |""".stripMargin
  }

}
