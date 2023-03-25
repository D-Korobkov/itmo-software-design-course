package com.github.d_korobkov.sd.actors
package stub

import sttp.tapir.Schema

import scala.util.Random

final case class SearchingResponseBody(searchingResults: List[SingleAnswerView])

object SearchingResponseBody {
  implicit final val TapirSchema: Schema[SearchingResponseBody] = Schema.derived
}

final case class SingleAnswerView(link: String, title: String, preview: String)

object SingleAnswerView {
  implicit final val TapirSchema: Schema[SingleAnswerView] = Schema.derived

  final def generate(rand: Random): SingleAnswerView = SingleAnswerView(
    link = repository.Urls(rand.nextInt(repository.Urls.length)),
    title = repository.Titles(rand.nextInt(repository.Titles.length)),
    preview = repository.Previews(rand.nextInt(repository.Previews.length)),
  )

  private object repository {

    final val Urls = List(
      "https://bells.example.net/beds/act.php?brother=bells&bed=addition",
      "https://www.example.com/boundary.php",
      "https://example.com/",
      "https://example.com/#bear",
      "https://example.edu/",
      "https://example.com/acoustics/balance#bed",
      "https://example.com/bikes.html",
      "https://bait.example.org/?arithmetic=attraction&brother=act",
      "https://www.example.com/",
      "https://example.com/beef",
      "https://blade.example.org/actor/birthday.php",
      "https://www.example.com/",
      "https://www.example.com/bottle.php",
      "https://example.com/?bell=animal&bridge=art",
      "https://www.example.net/",
      "https://example.org/ants.php",
      "https://www.example.com/?border=birds",
      "https://www.example.org/brake.html",
    )

    final val Titles = List(
      "operation",
      "politics",
      "midnight",
      "connection",
      "month",
      "criticism",
      "bath",
      "variation",
      "loss",
      "membership",
      "tea",
      "diamond",
      "performance",
      "concept",
      "internet",
      "variety",
      "injury",
      "foundation",
      "tale",
      "video",
    )

    final val Previews = List(
      "He found a leprechaun in his walnut shell.",
      "The tears of a clown make my lipstick run, but my shower cap is still intact.",
      "Toddlers feeding raccoons surprised even the seasoned park ranger.",
      "She is never happy until she finds something to be unhappy about; then, she is overjoyed.",
      "She was sad to hear that fireflies are facing extinction due to artificial light, habitat loss, and pesticides.",
      "There's no reason a hula hoop can't also be a circus ring.",
      "The body piercing didn 't go exactly as he expected.",
      "That is an appealing treasure map that I can't read.",
      "Poison ivy grew through the fence they said was impenetrable.",
      "It was a really good Monday for being a Saturday.",
      "The furnace repairman indicated the heating system was acting as an air conditioner.",
      "Tomatoes make great weapons when water balloons aren’t available.",
      "Sarah ran from the serial killer holding a jug of milk.",
      "Sometimes it is better to just walk away from things and go back to them later when you’re in a better frame of mind.",
      "The view from the lighthouse excited even the most seasoned traveler.",
      "He decided water - skiing on a frozen lake wasn’t a good idea.",
      "Mary plays the piano.",
      "With a single flip of the coin, his life changed forever.",
      "He dreamed of eating green apples with worms.",
      "You're unsure whether or not to trust him, but very thankful that you wore a turtle neck.",
    )

  }
}
