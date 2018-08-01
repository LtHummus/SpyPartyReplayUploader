package parsing

import java.io.{BufferedInputStream, DataInputStream, FileInputStream, InputStream}
import java.nio.{ByteBuffer, ByteOrder}
import java.util.Base64

import org.joda.time.DateTime
import parsing.versions.SpyPartyReplayDataExtractor
import scalaz.Scalaz._
import scalaz._

object GameResultEnum {
  sealed abstract class GameResult(val niceName: String, val internalId: Int) {
    override def toString: String = niceName
  }

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

import parsing.GameLoadoutTypeEnum._
import parsing.GameResultEnum._

object GameLoadoutTypeEnum {
  sealed abstract class GameLoadoutType(shortName: String)

  case object Known extends GameLoadoutType("k")
  case object Pick extends GameLoadoutType("p")
  case object Any extends GameLoadoutType("a")

  def fromInt(value: Int): String \/ GameLoadoutType = {
    value match {
      case 0 => Known.right
      case 1 => Pick.right
      case 2 => Any.right
      case _ => s"Invalid game type: $value".left
    }
  }
}

object GameType {
  def fromString(value: String): String \/ GameType = {
    val kind = value.charAt(0)
    val x = value.charAt(1) - 0x30

    kind match {
      case 'k' => GameType(GameLoadoutTypeEnum.Known, x, 0).right
      case 'a' => GameType(GameLoadoutTypeEnum.Any, x, value.charAt(3) - 0x30).right
      case 'p' => GameType(GameLoadoutTypeEnum.Pick, x, value.charAt(3) - 0x30).right
      case _ => "Unknown game type format".left
    }
  }

  def fromInt(value: Int): String \/ GameType = {
    val mode = value >> 28
    val y = (value & 0x0FFFC000) >> 14
    val x = value & 0x00003FFF

    for {
      gameType <- GameLoadoutTypeEnum.fromInt(mode)
    } yield GameType(gameType, x, y)
  }
}



case class GameType(kind: GameLoadoutType, x: Int, y: Int) {
  override def toString: String = kind match {
    case GameLoadoutTypeEnum.Known => s"k$x"
    case GameLoadoutTypeEnum.Any => s"a$x/$y"
    case GameLoadoutTypeEnum.Pick => s"p$x/$y"
  }
}

case class Replay(spy: String,
                  sniper: String,
                  startTime: DateTime,
                  result: GameResult,
                  level: Level,
                  loadoutType: GameType,
                  sequenceNumber: Int,
                  uuid: String,
                  version: Int,
                  startDuration: Option[Int],
                  numGuests: Option[Int]) extends Ordered[Replay] {
  override def compare(that: Replay): Int = if (this.startTime.isBefore(that.startTime)) -1 else 1

  def isCompleted: Boolean = result != GameResultEnum.InProgress
  def spyWon: Boolean = result == GameResultEnum.CivilianShot || result == GameResultEnum.MissionWin
  def sniperWon: Boolean = result == GameResultEnum.SpyShot || result == GameResultEnum.SpyTimeout

  def winnerName: String = if (spyWon) spy else sniper
  def winnerRole: String = if (spyWon) "spy" else "sniper"

  private def formatStartTime = startDuration.map(x => f"${x / 60}%d:${x % 60}%02d")

  private def additionalGameInfo = if (formatStartTime.isDefined && numGuests.isDefined) {
    s" ${formatStartTime.get} ${numGuests.get} guests"
  } else ""

  def fullLevelName = s"${level.name} $loadoutType$additionalGameInfo"

  def description: String = s"$winnerName wins as $winnerRole on $fullLevelName"
  def smallDescription: String = s"$winnerName wins as ${winnerRole.capitalize}"
}

object Replay {
  val HeaderDataSizeBytes = 416

  def fromInputStream(is: InputStream): String \/ Replay = {
    val headerData = new Array[Byte](HeaderDataSizeBytes)

    val bytesRead = is.read(headerData)

    if (bytesRead != HeaderDataSizeBytes) {
      return "Could not read entire replay data header".left
    }

    SpyPartyReplayDataExtractor.parseReplay(headerData)
  }

}
