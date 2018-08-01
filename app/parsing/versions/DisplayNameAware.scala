package parsing.versions

import scalaz.\/
import scalaz.syntax.either._

trait DisplayNameAware extends SpyPartyReplayDataExtractor {

  val spyUsernameLengthOffset: Int
  val sniperUsernameLengthOffset: Int
  val spyDisplayNameLengthOffset: Int
  val sniperDisplayNameLengthOffset: Int
  val playerNamesOffset: Int

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
}
