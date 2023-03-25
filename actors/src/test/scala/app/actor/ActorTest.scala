package com.github.d_korobkov.sd.actors
package app.actor

import app.model.typing.{Link, Preview, Title}
import app.model.{SearchingResults, SingleAnswer, Source}
import integration.{SearchingApi, SearchingResponseBody, SingleAnswerView}

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.mockito.ArgumentMatchers.{anyInt, anyString}
import org.mockito.Mockito.{verify, when}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.mockito.MockitoSugar.mock

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class ActorTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  sealed trait mocks {
    val mockGoogleApi: SearchingApi[Future] = mock[SearchingApi[Future]]
    val mockBingApi: SearchingApi[Future] = mock[SearchingApi[Future]]
  }

  sealed trait behavior extends mocks {
    def letGoogleApiReturns(response: SearchingResponseBody) = {
      when(mockGoogleApi.source).thenReturn(Source.Google)
      when(mockGoogleApi.search(anyString(), anyInt(), anyInt())).thenReturn(Future.successful(response))
    }

    def letBingApiReturns(response: SearchingResponseBody) = {
      when(mockBingApi.source).thenReturn(Source.Bing)
      when(mockBingApi.search(anyString(), anyInt(), anyInt())).thenReturn(Future.successful(response))
    }

    def letBingApiNeverReturnsAnswer() = {
      when(mockBingApi.source).thenReturn(Source.Bing)
      when(mockBingApi.search(anyString(), anyInt(), anyInt())).thenReturn(Future.never)
    }
  }

  "Child Actor" should {
    "get top 5 answers from API and terminate" in new behavior {
      letGoogleApiReturns(SearchingResponseBody(List.empty))

      val probe = testKit.createTestProbe[SearchingResults]()
      val childActor = testKit.spawn(ChildActor(mockGoogleApi, probe.ref))
      childActor.tell(ChildActor.Ask("what is this?"))

      val expected = SearchingResults.Success(Source.Google, List.empty)
      probe.expectMessage(expected)
      probe.expectTerminated(childActor)

      verify(mockGoogleApi).search(query = "what is this?", offset = 0, limit = 5)
    }
  }

  "Master Actor" should {
    "aggregate responses from each API and terminate" in new behavior {
      val googleAnswer = SingleAnswerView(Link("https://abc.com"), Title("Google"), Preview("Hello!"))
      val bingAnswer = SingleAnswerView(Link("https://def.com"), Title("Bing"), Preview("Hi!"))

      letGoogleApiReturns(SearchingResponseBody(List(googleAnswer)))
      letBingApiReturns(SearchingResponseBody(List(bingAnswer)))

      val probe = testKit.createTestProbe[List[SearchingResults]]()
      val masterActor = testKit.spawn(MasterActor(probe.ref, List(mockBingApi, mockGoogleApi), 1.minute))
      masterActor.tell(MasterActor.Ask("what is this?"))

      val expected = List(
        SearchingResults.Success(Source.Bing, List(SingleAnswer(Link("https://def.com"), Title("Bing"), Preview("Hi!")))),
        SearchingResults.Success(Source.Google, List(SingleAnswer(Link("https://abc.com"), Title("Google"), Preview("Hello!"))))
      )
      probe.receiveMessage() should contain theSameElementsAs expected
      probe.expectTerminated(masterActor)

      verify(mockGoogleApi).search(query = "what is this?", offset = 0, limit = 5)
      verify(mockBingApi).search(query = "what is this?", offset = 0, limit = 5)
    }

    "aggregate responses from APIs which respond before timeout and terminate" in new behavior {
      val googleAnswer = SingleAnswerView(Link("https://abc.com"), Title("Google"), Preview("Hello!"))

      letGoogleApiReturns(SearchingResponseBody(List(googleAnswer)))
      letBingApiNeverReturnsAnswer()

      val probe = testKit.createTestProbe[List[SearchingResults]]()
      val masterActor = testKit.spawn(MasterActor(probe.ref, List(mockBingApi, mockGoogleApi), 1.second))
      masterActor.tell(MasterActor.Ask("what is this?"))

      val expected = List(
        SearchingResults.Success(Source.Google, List(SingleAnswer(Link("https://abc.com"), Title("Google"), Preview("Hello!"))))
      )
      probe.receiveMessage() should contain theSameElementsAs expected
      probe.expectTerminated(masterActor)

      verify(mockGoogleApi).search(query = "what is this?", offset = 0, limit = 5)
      verify(mockBingApi).search(query = "what is this?", offset = 0, limit = 5)
    }
  }

}
