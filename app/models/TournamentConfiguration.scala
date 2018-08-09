package models

import play.api.libs.json.Json

case class TournamentConfiguration(shouldScoreMatch: Boolean, allowTies: Option[Boolean], pointsToWin: Option[Int]) {
  private def valid: Boolean = {
    if (!shouldScoreMatch) {
      true
    } else {
      allowTies.isDefined && pointsToWin.isDefined
    }
  }

  require(valid)
}
object TournamentConfiguration {
  //potential TODO: override the decoding here to validate that allowTies and pointsToWin are set iff shouldScoreMatch
  implicit val format = Json.format[TournamentConfiguration]
}
