package models

import play.api.libs.json.Json
import replays.GameResultEnum.GameResult

case class Game(id: Int,
                bout: Int,
                spy: String,
                sniper: String,
                result: GameResult,
                level: String,
                loadout: String,
                uuid: String,
                version: Int,
                selectedMissions: List[String],
                pickedMissions: List[String],
                accomplishedMissions: List[String],
                startDurationSeconds: Option[Int],
                numGuests: Option[Int])

object Game {
  implicit def format = Json.format[Game]
}