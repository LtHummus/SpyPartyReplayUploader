package models

import play.api.libs.json.Json

case  class BoutMetadataItem(key: String, value: String)
object BoutMetadataItem {
  implicit val format = Json.format[BoutMetadataItem]
}

case class BoutMetadata(items: Seq[BoutMetadataItem])
object BoutMetadata {
  implicit val format = Json.format[BoutMetadata]
}

case class Bout(id: Int, tournament: Int, player1: String, player2: String, url: String, metadata: BoutMetadata)
object Bout {
  implicit val format = Json.format[Bout]
}