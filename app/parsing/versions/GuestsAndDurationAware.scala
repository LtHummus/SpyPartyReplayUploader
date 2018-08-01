package parsing.versions

import scalaz.\/
import scalaz.syntax.either._

trait GuestsAndDurationAware extends SpyPartyReplayDataExtractor {

  val startDurationOffset: Int
  val numGuestsOffset: Int

  import SpyPartyReplayDataExtractor._

  override def numGuests: String \/ Option[Int] = {
    Some(extractInt(headerData, numGuestsOffset)).right
  }

  override def startDuration: String \/ Option[Int] = {
    Some(extractInt(headerData, startDurationOffset)).right
  }
}
