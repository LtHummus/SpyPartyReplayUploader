package models

import play.api.libs.json.Json

case class Tournament(id: Int, name: String, active: Boolean, formItems: TournamentSubmissionForm)
object Tournament {
  implicit val format = Json.format[Tournament]
}

case class TournamentInput(name: String, active: Boolean, formItems: TournamentSubmissionForm)
object TournamentInput {
  implicit val format = Json.format[TournamentInput]
}