package replays

import play.api.libs.json._
import scalaz.\/
import scalaz.syntax.either._

object GameResultEnum {
  sealed abstract class GameResult(val niceName: String, val internalId: Int) {
    override def toString: String = niceName
  }

  private val writes: Writes[GameResult] = (x: GameResult) => {
    JsString(x.toString)
  }

  private val reads: Reads[GameResult] = (json: JsValue) => {
    json.as[String] match {
      case "Mission Win"   => JsSuccess(MissionWin)
      case "Spy Timeout"   => JsSuccess(SpyTimeout)
      case "Spy Shot"      => JsSuccess(SpyShot)
      case "Civilian Shot" => JsSuccess(CivilianShot)
      case "In Progress"   => JsSuccess(InProgress)
      case _               => JsError("Unknown game result")
    }
  }

  implicit val format: Format[GameResult] = Format(reads, writes)

  case object MissionWin extends GameResult("Mission Win", 0)
  case object SpyTimeout extends GameResult("Spy Timeout", 1)
  case object SpyShot extends GameResult("Spy Shot", 2)
  case object CivilianShot extends GameResult("Civilian Shot", 3)
  case object InProgress extends GameResult("In Progress", 4)

  def fromString(value: String): String \/ GameResult = {
    value match {
      case "Mission Win"   => MissionWin.right
      case "Spy Timeout"   => SpyTimeout.right
      case "Spy Shot"      => SpyShot.right
      case "Civilian Shot" => CivilianShot.right
      case "In Progress"   => InProgress.right
      case _               => s"Unknown game result type: $value".left
    }
  }

  def fromInt(value: Int): String \/ GameResult = {
    value match {
      case 0 => MissionWin.right
      case 1 => SpyTimeout.right
      case 2 => SpyShot.right
      case 3 => CivilianShot.right
      case 4 => InProgress.right
      case _ => s"Unknown game result type: $value".left
    }
  }
}