package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, JsValue, Json, Writes}

case class SelectionEntry(internalName: String, displayName: String)
object SelectionEntry {
  implicit def format = Json.format[SelectionEntry]
}


//XXX: we should probably do an enum sort of thing for this, but JSON serializion is hardddddd
sealed trait TournamentFormKindMetadata {
  val kind: String
}
case class StringMetadata(maxLength: Int) extends TournamentFormKindMetadata {
  override val kind: String = "String"
}
object StringMetadata {
  implicit def format = Json.format[StringMetadata]
}

case class NumberMetadata(min: Int, max: Int) extends TournamentFormKindMetadata {
  override val kind: String = "Number"
}
object NumberMetadata {
  implicit def format = Json.format[NumberMetadata]
}

case class SelectionMetadata(items: Seq[SelectionEntry]) extends TournamentFormKindMetadata {
  override val kind: String = "Selection"
}
object SelectionMetadata {
  implicit def format = Json.format[SelectionMetadata]
}

case class TournamentSubmissionFormItem(internalName: String, displayName: String, metadata: TournamentFormKindMetadata)
object TournamentSubmissionFormItem {
  private val writes: Writes[TournamentSubmissionFormItem] = (
    (JsPath \ "internalName").write[String] and
    (JsPath \ "displayName").write[String]
  )(x => (x.internalName, x.displayName))

  private val reads: Reads[TournamentSubmissionFormItem] = (
    (JsPath \ "internalName").read[String] and
    (JsPath \ "displayName").read[String]
  )((i, d) => TournamentSubmissionFormItem(i, d, StringMetadata(4)))

  implicit val format: Format[TournamentSubmissionFormItem] = Format(reads, writes)
}

case class TournamentSubmissionForm(items: Seq[TournamentSubmissionFormItem])
object TournamentSubmissionForm {
  implicit def format = Json.format[TournamentSubmissionForm]
}