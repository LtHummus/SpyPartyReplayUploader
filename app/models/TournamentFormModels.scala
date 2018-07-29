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
object TournamentFormKindMetadata {
  private val writes: Writes[TournamentFormKindMetadata] = new Writes[TournamentFormKindMetadata] {
    override def writes(o: TournamentFormKindMetadata): JsValue = {
      o match {
        case x: StringMetadata =>
          JsObject(
            Seq(
              "kind" -> JsString(x.kind),
              "maxLength" -> JsNumber(x.maxLength)
            )
          )
        case x: NumberMetadata =>
          JsObject(
            Seq(
              "kind" -> JsString(x.kind),
              "min" -> JsNumber(x.min),
              "max" -> JsNumber(x.max)
            )
          )
        case x: SelectionMetadata =>
          JsObject(
            Seq(
              "kind" -> JsString(x.kind),
              "items" -> Json.toJson(x.entries)
            )
          )
      }
    }
  }
  private val reads: Reads[TournamentFormKindMetadata] = new Reads[TournamentFormKindMetadata] {
    override def reads(json: JsValue): JsResult[TournamentFormKindMetadata] = {
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
  }

  implicit val format = Format(reads, writes)
}
case class StringMetadata(maxLength: Int) extends TournamentFormKindMetadata {
  override val kind: String = "String"
}
case class NumberMetadata(min: Int, max: Int) extends TournamentFormKindMetadata {
  override val kind: String = "Number"
}
case class SelectionMetadata(entries: Seq[SelectionEntry]) extends TournamentFormKindMetadata {
  override val kind: String = "Selection"
}

case class TournamentSubmissionFormItem(internalName: String, displayName: String, metadata: TournamentFormKindMetadata)
object TournamentSubmissionFormItem {
  private val writes: Writes[TournamentSubmissionFormItem] = new Writes[TournamentSubmissionFormItem] {
    override def writes(o: TournamentSubmissionFormItem): JsValue = {
      JsObject(
        Seq(
          "displayName" -> JsString(o.displayName),
          "internalName" -> JsString(o.internalName),
          "metadata" -> Json.toJson(o.metadata)
        )
      )
    }
  }

  private val reads: Reads[TournamentSubmissionFormItem] = new Reads[TournamentSubmissionFormItem] {
    override def reads(json: JsValue): JsResult[TournamentSubmissionFormItem] = {
      val internalName = (json \ "internalName").as[String]
      val displayName = (json \ "displayName").as[String]
      val metadata = (json \ "metadata").as[TournamentFormKindMetadata]

      JsSuccess(TournamentSubmissionFormItem(internalName, displayName, metadata))
    }
  }

  implicit val format: Format[TournamentSubmissionFormItem] = Format(reads, writes)
}

case class TournamentSubmissionForm(items: Seq[TournamentSubmissionFormItem])
object TournamentSubmissionForm {
  implicit def format = Json.format[TournamentSubmissionForm]
}