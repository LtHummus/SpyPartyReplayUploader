package replays

import replays.GameLoadoutTypeEnum.GameLoadoutType
import scalaz.\/
import scalaz.syntax.either._


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