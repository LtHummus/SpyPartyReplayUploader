package models

import play.api.libs.json.Json

case class TournamentBoutInfo(tournamentId: Int, numberOfBouts: Int, bouts: Seq[Bout])
object TournamentBoutInfo {
  implicit val formats = Json.format[TournamentBoutInfo]
}
