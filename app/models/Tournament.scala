package models

import play.api.libs.json.Json
import scalaz.\/
import scalaz.syntax.either._

case class Tournament(id: Int, name: String, active: Boolean, formItems: TournamentSubmissionForm, configuration: TournamentConfiguration) {
  def validate(data: Map[String, String]): String \/ BoutMetadata = {
    val requiredFieldNames = formItems.items.map(_.internalName).toSet
    val fieldsWeHave = data.keys.toSet

    val missingFields = requiredFieldNames -- fieldsWeHave

    if (missingFields.nonEmpty) {
      s"Fields missing: missingFields".left
    } else {
      //here, validate everything, since we know we've got it
      val failed = formItems.items.filterNot { curr =>
        val input = data(curr.internalName)
        curr.metadata.validate(input)
      }.map(_.internalName)

      if (failed.nonEmpty) {
        s"Failed validation: ${failed.mkString(", ")}".left
      } else {
        BoutMetadata(formItems.items.map(_.internalName).map(it => BoutMetadataItem(it, data(it)))).right
      }
    }
  }
}
object Tournament {
  implicit val format = Json.format[Tournament]
}

case class TournamentInput(name: String, active: Boolean, formItems: TournamentSubmissionForm, configuration: TournamentConfiguration)
object TournamentInput {
  implicit val format = Json.format[TournamentInput]
}