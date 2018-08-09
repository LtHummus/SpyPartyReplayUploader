package replays.versions

import java.nio.{ByteBuffer, ByteOrder}
import java.util.Base64

import org.joda.time.DateTime
import replays._
import replays.GameResultEnum.GameResult
import scalaz.\/
import scalaz.syntax.either._

trait SpyPartyReplayDataExtractor {
  import SpyPartyReplayDataExtractor._

  val headerData: Array[Byte]

  val versionNumber: Int

  val protocolVersionOffset: Int
  val spyPartyVersionOffset: Int
  val durationOffset: Int
  val uuidOffset: Int
  val timestampOffset: Int
  val sequenceNumberOffset: Int
  val spyUsernameLengthOffset: Int
  val sniperUsernameLengthOffset: Int
  val gameResultOffset: Int
  val gameTypeOffset: Int
  val levelOffset: Int
  val playerNamesOffset: Int
  val selectedMissionsOffset: Int
  val pickedMissionsOffset: Int
  val completedMissionsOffset: Int

  def gameResult: String \/ GameResult = {
    for {
      gameResult <- GameResultEnum.fromInt(headerData(gameResultOffset))
    } yield gameResult
  }

  def startTime: String \/ DateTime = {
    val timeBytes = headerData.slice(timestampOffset, timestampOffset + 4)
    val buffer = ByteBuffer.wrap(timeBytes)
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    val res = buffer.getInt() & 0xFFFFFFFFL

    new DateTime(res * 1000).right
  }

  def spyName: String \/ String = {
    val spyNameLength = headerData(spyUsernameLengthOffset)
    new String(headerData.slice(playerNamesOffset, playerNamesOffset + spyNameLength), "UTF-8").right
  }

  def sniperName: String \/ String = {
    val spyNameLength = headerData(spyUsernameLengthOffset)
    val sniperNameLength = headerData(sniperUsernameLengthOffset)
    new String(headerData.slice(playerNamesOffset + spyNameLength, playerNamesOffset + spyNameLength + sniperNameLength), "UTF-8").right
  }

  def gameType: String \/ GameType = {
    val value = extractInt(headerData, gameTypeOffset)

    val mode = value >> 28
    val y = (value & 0x0FFFC000) >> 14
    val x = value & 0x00003FFF

    for {
      gameType <- GameLoadoutTypeEnum.fromInt(mode)
    } yield GameType(gameType, x, y)
  }

  def uuid: String \/ String = {
    Base64
      .getEncoder
      .encodeToString(headerData.slice(uuidOffset, uuidOffset + 16)) //encode bytes to base64 string
      .split("=")(0)                                     //drop trailing = signs
      .replaceAll("\\+", "-")                            //replace + with - because that's how spyparty does things
      .replaceAll("/", "_")                              //replace / with _ ibid
      .right
  }

  def level: String \/ Level = {
    Level.getLevelByChecksum(extractInt(headerData, levelOffset))
  }

  def sequenceNumber: String \/ Int = {
    extractShort(headerData, sequenceNumberOffset).toInt.right
  }

  def selectedMissions: String \/ List[Mission] = Mission.listFromInt(extractInt(headerData, selectedMissionsOffset)).right

  def pickedMissions: String \/ List[Mission] = Mission.listFromInt(extractInt(headerData, pickedMissionsOffset)).right

  def completedMissions: String \/ List[Mission] = Mission.listFromInt(extractInt(headerData, completedMissionsOffset)).right

  def numGuests: String \/ Option[Int] = None.right

  def startDuration: String \/ Option[Int] = None.right

  def toReplay: String \/ Replay = {

    for {
      gameResult        <- gameResult
      startTime         <- startTime
      spy               <- spyName
      sniper            <- sniperName
      gameType          <- gameType
      uuid              <- uuid
      level             <- level
      sequence          <- sequenceNumber
      numGuests         <- numGuests
      startDuration     <- startDuration
      pickedMissions    <- pickedMissions
      selectedMissions  <- selectedMissions
      completedMissions <- completedMissions
    } yield Replay(spy,
      sniper,
      startTime,
      gameResult,
      level,
      gameType,
      sequence,
      uuid,
      versionNumber,
      selectedMissions,
      pickedMissions,
      completedMissions,
      numGuests,
      startDuration)
  }

}

object SpyPartyReplayDataExtractor {
  val magicNumberOffset: Int = 0x00
  val fileVersionOffset: Int = 0x04

  def parseReplay(headerData: Array[Byte]): String \/ Replay = {
    for {
      _           <- verifyMagicNumber(headerData)
      fileVersion <- getFileVersion(headerData)
      extractor   <- getExtractor(headerData, fileVersion)
      replay      <- extractor.toReplay
    } yield replay
  }

  def getExtractor(headerData: Array[Byte], version: Int): String \/ SpyPartyReplayDataExtractor = {
    version match {
      case 3 => ReplayVersion3DataExtractor(headerData).right
      case 4 => ReplayVersion4DataExtractor(headerData).right
      case 5 => ReplayVersion5DataExtractor(headerData).right
      case _ => s"Unknown SpyParty replay header version: $version".left
    }
  }

  def verifyMagicNumber(data: Array[Byte]): String \/ String = {
    if (data(0) == 'R' && data(1) == 'P' && data(2) == 'L' && data(3) == 'Y')
      "Magic Number OK".right
    else
      "Magic number incorrect".left
  }

  def getFileVersion(data: Array[Byte]): String \/ Int = {
    extractInt(data, fileVersionOffset).right
  }

  def extractInt(data: Array[Byte], index: Int): Int = {
    val buffer = ByteBuffer.wrap(data.slice(index, index + 4))
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.getInt
  }

  def extractShort(data: Array[Byte], index: Int): Short = {
    val buffer = ByteBuffer.wrap(data.slice(index, index + 2))
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    buffer.getShort
  }
}
