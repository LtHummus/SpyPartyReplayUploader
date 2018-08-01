package parsing.versions
import scalaz.\/

import scalaz.syntax.either._

case class ReplayVersion5DataExtractor(headerData: Array[Byte]) extends SpyPartyReplayDataExtractor {
  import SpyPartyReplayDataExtractor._

  override val versionNumber: Int = 5

  override val protocolVersionOffset: Int      = 0x08
  override val spyPartyVersionOffset: Int      = 0x0C
  override val durationOffset: Int             = 0x14
  override val uuidOffset: Int                 = 0x18
  override val timestampOffset: Int            = 0x28
  override val sequenceNumberOffset: Int       = 0x2C
  override val spyUsernameLengthOffset: Int    = 0x2E
  override val sniperUsernameLengthOffset: Int = 0x2F
  override val gameResultOffset: Int           = 0x38
  override val gameTypeOffset: Int             = 0x3C
  override val levelOffset: Int                = 0x40
  override val playerNamesOffset: Int          = 0x60

  val spyDisplayNameLengthOffset = 0x30
  val sniperDisplayNameLengthOffset = 0x31
  val startDurationOffset = 0x50
  val numGuestsOffset = 0x54

  //a musing for the future: right now, we only care about display names...should we care about usernames???
  override def spyName: String \/ String = {
    val spyUsernameLength = headerData(spyUsernameLengthOffset)
    val spyDisplayNameLength = headerData(spyDisplayNameLengthOffset)
    if (spyDisplayNameLength == 0) {
      //we don't have a display name, return the username
      new String(headerData.slice(playerNamesOffset, playerNamesOffset + spyUsernameLength), "UTF-8").right
    } else {
      //we need to get the display name...which is after the spy and sniper usernames
      val sniperUsernameLength = headerData(sniperUsernameLengthOffset)
      new String(headerData.slice(playerNamesOffset + spyUsernameLength + sniperUsernameLength,
        playerNamesOffset + spyUsernameLength + sniperUsernameLength + spyDisplayNameLength)).right
    }
  }

  override def sniperName: String \/ String = {
    val spyUsernameLength = headerData(spyUsernameLengthOffset)
    val spyDisplayNameLength = headerData(spyDisplayNameLengthOffset)
    val sniperUsernameLength = headerData(sniperUsernameLengthOffset)
    val sniperDisplayNameLength = headerData(sniperDisplayNameLengthOffset)
    if (sniperDisplayNameLength == 0) {
      new String(headerData.slice(playerNamesOffset + spyUsernameLength,
        playerNamesOffset + spyUsernameLength + sniperUsernameLength), "UTF-8").right
    } else {
      new String(headerData.slice(playerNamesOffset + spyUsernameLength + sniperUsernameLength + spyDisplayNameLength,
        playerNamesOffset + spyUsernameLength + sniperUsernameLength + spyDisplayNameLength + sniperDisplayNameLength
      )).right
    }
  }

  override def numGuests: String \/ Option[Int] = {
    Some(extractInt(headerData, numGuestsOffset)).right
  }

  override def startDuration: String \/ Option[Int] = {
    Some(extractInt(headerData, startDurationOffset)).right
  }

}
