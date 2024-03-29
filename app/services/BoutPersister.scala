package services

import java.nio.file.Path

import database.{BulkPersistUtil, TournamentDao}
import javax.inject.{Inject, Singleton}
import models._
import scalaz._
import Scalaz._
import play.api.libs.json._
import org.apache.commons.io.IOUtils
import replays.Replay
import replays._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

sealed trait PersistResultKind
object PersistResultKind {
  private val writes: Writes[PersistResultKind] = (k: PersistResultKind) => {
    JsString(k.toString)
  }
  private val reads: Reads[PersistResultKind] = (json: JsValue) => {
    fromString(json.as[String]) match {
      case Some(kind) => JsSuccess(kind)
      case _          => JsError("Unknown result kind")
    }

  }

  private def fromString(x: String): Option[PersistResultKind] = {
    x match {
      case "BadTournamentId"       => Some(BadTournamentId)
      case "BadMetadata"           => Some(BadMetadata)
      case "InvalidZipFile"        => Some(InvalidZipFile)
      case "FailedToAddToDatabase" => Some(FailedToAddToDatabase)
      case "UploadFailure"         => Some(UploadFailure)
      case "Successful"            => Some(Successful)
      case _                       => None
    }
  }

  implicit val format: Format[PersistResultKind] = Format(reads, writes)
}
case object BadTournamentId extends PersistResultKind
case object BadMetadata extends PersistResultKind
case object InvalidZipFile extends PersistResultKind
case object FailedToAddToDatabase extends PersistResultKind
case object UploadFailure extends PersistResultKind
case object Successful extends PersistResultKind

case class PersistResult(kind: PersistResultKind, message: String)
object PersistResult {
  implicit val format = Json.format[PersistResult]
}


@Singleton
class BoutPersister @Inject()(uploader: FileUploader, tournamentDao: TournamentDao, bulkInserter: BulkPersistUtil, zipParser: SpyPartyZipParser)(implicit ec: ExecutionContext) {

  private def getTournament(data: Map[String, String]): Future[PersistResult \/ Tournament] = {
    data.get("tournament") match {
      case None               => Future.successful(PersistResult(BadTournamentId, s"Unknown tournament id ${data.get("tournament")}").left)
      case Some(tournamentId) => tournamentDao.getById(tournamentId.toInt).map {
        case None             => PersistResult(BadTournamentId, s"Unknown tournament id ${data.get("tournament")}").left
        case Some(tournament) => tournament.right
      }
    }
  }

  private def validateData(tournament: Tournament, data: Map[String, String]): Future[PersistResult \/ BoutMetadata] = {
    Future.successful {
      tournament.validate(data).leftMap(msg => PersistResult(BadMetadata, msg))
    }
  }

  private def addToDatabase(tournamentId: Int,
                            player1: String,
                            player2: String,
                            url: String,
                            metadata: BoutMetadata,
                            replays: List[Replay]): Future[PersistResult \/ Int] = {
    val b = Bout(0, tournamentId, player1, player2, url, metadata)
    bulkInserter.persistAllBoutData(b, replays).transformWith {
      case Success(res) => Future.successful(res.right)
      case Failure(_)   => Future.successful(PersistResult(FailedToAddToDatabase, "Failed to add bout to database").left)
    }
  }


  private def uploadFile(source: Path, player1: String, player2: String, tournament: Tournament): Future[PersistResult \/ String] = {
    val fileKey = s"${tournament.name}/$player1 vs $player2 (${System.currentTimeMillis()}).zip" //i really don't like the timestamp...should change it later
    uploader.uploadFile(source, fileKey).map { res =>
      res.leftMap(_ => PersistResult(UploadFailure, "Unable to upload replay to S3"))
    }
  }

  private def parseZipFile(source: Path): Future[PersistResult \/ List[Replay]] = {
    Future {
      IOUtils.toByteArray(source.toUri)
    }.map { data => zipParser.parseZipStream(data).leftMap(x => PersistResult(InvalidZipFile, x)) }

  }

  private def validateReplays(replays: List[Replay], tournament: Tournament): Future[PersistResult \/ Unit] = {
    if (tournament.configuration.shouldScoreMatch) {
      //force unwrap because if we're in here, these _MUST_ be defined
      val tiesAllowed = tournament.configuration.allowTies.get
      val pointsToWin = tournament.configuration.pointsToWin.get

      if (replays.isTie && !tiesAllowed) {
        Future.successful(PersistResult(InvalidZipFile, "Replays end in a tie, but ties aren't allowed").left)
      } else {
        val winnerScore = Math.max(replays.player1Score, replays.player2Score)
        if (winnerScore < pointsToWin) {
          Future.successful(PersistResult(InvalidZipFile, s"Winner only won $winnerScore games, but needs $pointsToWin").left)
        } else {
          Future.successful(().right)
        }
      }

    } else {
      Future.successful(().right)
    }
  }

  def persist(data: Map[String, Seq[String]], file: Path)(implicit ec: ExecutionContext): Future[PersistResult] = {
    //since this is URLEncoded data, it is possible for each key to map to multiple values, we don't care about that, so
    //just flatten everything out by taking the first
    val fixedData = data.mapValues(_.head)

    //FIXME: this should really be done in a transaction...
    (for {
      tournament <- EitherT(getTournament(fixedData))
      metadata   <- EitherT(validateData(tournament, fixedData))
      replays    <- EitherT(parseZipFile(file))
      _          <- EitherT(validateReplays(replays, tournament))
      uploadFile <- EitherT(uploadFile(file, replays.player1, replays.player2, tournament))
      boutId     <- EitherT(addToDatabase(tournament.id, replays.player1, replays.player2, uploadFile, metadata, replays))

    } yield PersistResult(Successful, s"Bout $boutId persisted successfully")).run.map(_.merge)
  }

}
