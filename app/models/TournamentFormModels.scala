package models

import play.api.libs.json._
import play.api.libs.json.{JsValue, Json, Writes}

case class SelectionEntry(internalName: String, displayName: String)
object SelectionEntry {
  implicit def format = Json.format[SelectionEntry]
}


//TODO: this can have a serialier/deserializer itself to make things even more typesafe....
sealed abstract class TournamentFormKind(val name: String)
case object String extends TournamentFormKind("String")
case object Number extends TournamentFormKind("Number")
case object Selection extends TournamentFormKind("Selection")

sealed abstract class TournamentFormKindMetadata {
  val kind: TournamentFormKind

  def validate(input: String): Boolean
}
object TournamentFormKindMetadata {
  private val writes: Writes[TournamentFormKindMetadata] = {
    case x: StringMetadata =>
      JsObject(
        Seq(
          "kind" -> JsString(x.kind.name),
          "maxLength" -> JsNumber(x.maxLength)
        )
      )
    case x: NumberMetadata =>
      JsObject(
        Seq(
          "kind" -> JsString(x.kind.name),
          "min" -> JsNumber(x.min),
          "max" -> JsNumber(x.max)
        )
      )
    case x: SelectionMetadata =>
      JsObject(
        Seq(
          "kind" -> JsString(x.kind.name),
          "items" -> Json.toJson(x.entries)
        )
      )
  }
  private val reads: Reads[TournamentFormKindMetadata] = (json: JsValue) => {
    (json \ "kind").get match {
      case JsString("String") =>
        JsSuccess(StringMetadata((json \ "maxLength").as[Int]))
      case JsString("Number") =>
        val min = (json \ "min").as[Int]
        val max = (json \ "max").as[Int]
        JsSuccess(NumberMetadata(min, max))
      case JsString("Selection") =>
        val items = (json \ "items").as[Seq[SelectionEntry]]
        JsSuccess(SelectionMetadata(items))
      case _ =>
        JsError("Unknown metadata type in JSON Structure")
    }
  }

  implicit val format = Format(reads, writes)
}
case class StringMetadata(maxLength: Int) extends TournamentFormKindMetadata {
  override val kind = String

  override def validate(input: String): Boolean = input.length <= maxLength
}
case class NumberMetadata(min: Int, max: Int) extends TournamentFormKindMetadata {
  override val kind = Number

  override def validate(input: String): Boolean = {
    val num = input.toInt
    //TODO: fail this if we're not a number
    min <= num && num <= max
  }
}
case class SelectionMetadata(entries: Seq[SelectionEntry]) extends TournamentFormKindMetadata {
  override val kind = Selection

  private val possibleEntryValues = entries.map(_.internalName).toSet

  override def validate(input: String): Boolean = possibleEntryValues.contains(input)
}

case class TournamentSubmissionFormItem(internalName: String, displayName: String, metadata: TournamentFormKindMetadata)
object TournamentSubmissionFormItem {
  private val writes: Writes[TournamentSubmissionFormItem] = (o: TournamentSubmissionFormItem) => {
    JsObject(
      Seq(
        "displayName" -> JsString(o.displayName),
        "internalName" -> JsString(o.internalName),
        "metadata" -> Json.toJson(o.metadata)
      )
    )
  }

  private val reads: Reads[TournamentSubmissionFormItem] = (json: JsValue) => {
    val internalName = (json \ "internalName").as[String]
    val displayName = (json \ "displayName").as[String]
    val metadata = (json \ "metadata").as[TournamentFormKindMetadata]

    JsSuccess(TournamentSubmissionFormItem(internalName, displayName, metadata))
  }

  implicit val format: Format[TournamentSubmissionFormItem] = Format(reads, writes)
}

case class TournamentSubmissionForm(items: Seq[TournamentSubmissionFormItem])
object TournamentSubmissionForm {
  implicit def format = Json.format[TournamentSubmissionForm]
}