package models

import play.api.libs.json.{JsValue, Json}

case class Bout(id: Int, tournament: Int, player1: String, player2: String, url: String, metadata: JsValue)
object Bout {
  implicit val format = Json.format[Bout]
}