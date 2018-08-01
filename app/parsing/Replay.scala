package parsing

import java.io.InputStream

import org.joda.time.DateTime
import parsing.versions.SpyPartyReplayDataExtractor
import scalaz.\/
import scalaz.syntax.either._

import parsing.GameResultEnum._


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
