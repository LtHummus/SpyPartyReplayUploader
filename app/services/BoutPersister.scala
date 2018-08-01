package services

import java.nio.file.Path

import database.{BoutDao, TournamentDao}
import javax.inject.{Inject, Singleton}
import models._
import scalaz._
import Scalaz._
import play.api.libs.json.Json

import io.leonard.TraitFormat._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

sealed trait PersistResultKind
object PersistResultKind {
  implicit val format = traitFormat[PersistResultKind] <<
    caseObjectFormat(BadTournamentId) <<
    caseObjectFormat(BadMetadata) <<
    caseObjectFormat(InvalidZipFile) <<
    caseObjectFormat(FailedToAddToDatabase) <<
    caseObjectFormat(UploadFailure) <<
    caseObjectFormat(Successful)
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
class BoutPersister @Inject() (uploader: FileUploader, boutDao: BoutDao, tournamentDao: TournamentDao)(implicit ec: ExecutionContext) {

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

  private def addToDatabase(tournamentId: Int, player1: String, player2: String, url: String, metadata: BoutMetadata): Future[PersistResult \/ Int] = {
    val b = Bout(0, tournamentId, player1, player2, url, metadata)
    boutDao.insert(b).transformWith {
      case Success(res) => Future.successful(res.right)
      case Failure(_) => Future.successful(PersistResult(FailedToAddToDatabase, "Failed to add bout to database").left)
    }
  }

  private def uploadFile(source: Path): Future[PersistResult \/ String] = {
    uploader.uploadFile(source, "hello").map { res =>
      res.leftMap(_ => PersistResult(UploadFailure, "Unable to upload replay to S3"))
    }
  }

  def persist(data: Map[String, Seq[String]], file: Path)(implicit ec: ExecutionContext): Future[PersistResult] = {
    //since this is URLEncoded data, it is possible for each key to map to multiple values, we don't care about that, so
    //just flatten everything out by taking the first
    val fixedData = data.mapValues(_.head)


    (for {
      tournament <- EitherT(getTournament(fixedData))
      metadata   <- EitherT(validateData(tournament, fixedData))
      //TODO: parse replays here to get player information
      uploadFile <- EitherT(uploadFile(file))
      _          <- EitherT(addToDatabase(tournament.id, "", "", uploadFile, metadata))
    } yield PersistResult(Successful, "Bout persisted successfully")).run.map(_.merge)
  }

}
