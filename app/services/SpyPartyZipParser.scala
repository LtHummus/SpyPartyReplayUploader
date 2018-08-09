package services

import java.io.{ByteArrayInputStream, DataInputStream}
import java.util.zip.{ZipEntry, ZipInputStream}

import javax.inject.Singleton
import replays.Replay
import scalaz.{-\/, \/, \/-}
import scalaz.syntax.either._

import scala.concurrent.ExecutionContext

@Singleton
class SpyPartyZipParser {

  def parseZipStream(bytes: Array[Byte])(implicit ec: ExecutionContext): String \/ List[Replay] = {
    val zis = new ZipInputStream(new ByteArrayInputStream(bytes))

    val replays = scala.collection.mutable.ListBuffer[Replay]()
    var entry: ZipEntry = zis.getNextEntry
    val errors = scala.collection.mutable.ListBuffer[String]()

    while (entry != null) {
      if (!entry.isDirectory) {
        val parsed = Replay.fromInputStream(new DataInputStream(zis))
        parsed match {
          case -\/(msg) => errors += ("Could not parse replay: " + msg)
          case \/-(replay) => replays += replay
        }
      }
      entry = zis.getNextEntry
    }

    zis.close()

    try {
      if (errors.nonEmpty) {
        ("Errors parsing replays in ZIP file: " + errors.mkString(", ")).left
      } else if (replays.isEmpty)
        "No replays found in ZIP file".left
      else
        replays.toList.right
    } catch {
      case e: Exception => e.getMessage.left
    }
  }

}
