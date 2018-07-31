package services

import java.nio.file.Path

import database.{BoutDao, TournamentDao}
import javax.inject.{Inject, Singleton}
import models._
import scalaz._
import Scalaz._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

sealed trait PersistResult
case object BadTournamentId extends PersistResult
case object MissingMetadata extends PersistResult
@Deprecated case object BadMetadata extends PersistResult //TODO: this shouldn't be used in favor of more detailed messages
case object InvalidSelection extends PersistResult
case object InvalidZipFile extends PersistResult
case object FailedToAddToDatabase extends PersistResult
case object UploadFailure extends PersistResult
case object Successful extends PersistResult


@Singleton
class BoutPersister @Inject() (uploader: FileUploader, boutDao: BoutDao, tournamentDao: TournamentDao)(implicit ec: ExecutionContext) {

  private def getTournament(data: Map[String, String]): Future[PersistResult \/ Tournament] = {
    data.get("tournament") match {
      case None               => Future.successful(BadTournamentId.left)
      case Some(tournamentId) => tournamentDao.getById(tournamentId.toInt).map {
        case None             => BadTournamentId.left
        case Some(tournament) => tournament.right
      }
    }
  }

  private def validateData(tournament: Tournament, data: Map[String, String]): Future[PersistResult \/ BoutMetadata] = {
    Future.successful {
      tournament.validate(data).leftMap(_ => BadMetadata)
    }
  }

  private def addToDatabase(tournamentId: Int, player1: String, player2: String, url: String, metadata: BoutMetadata): Future[PersistResult \/ Int] = {
    val b = Bout(0, tournamentId, player1, player2, url, metadata)
    boutDao.insert(b).transformWith {
      case Success(res) => Future.successful(res.right)
      case Failure(_) => Future.successful(FailedToAddToDatabase.left)
    }
  }

  private def uploadFile(source: Path): Future[PersistResult \/ String] = {
    uploader.uploadFile(source, "hello").map { res =>
      res.leftMap(_ => UploadFailure)
    }
  }

  def persist(data: Map[String, Seq[String]], file: Path)(implicit ec: ExecutionContext): Future[PersistResult \/ PersistResult] = {
    //since this is URLEncoded data, it is possible for each key to map to multiple values, we don't care about that, so
    //just flatten everything out by taking the first
    val fixedData = data.mapValues(_.head)


    (for {
      tournament <- EitherT(getTournament(fixedData))
      metadata   <- EitherT(validateData(tournament, fixedData))
      //TODO: parse replays here to get player information
      uploadFile <- EitherT(uploadFile(file))
      _          <- EitherT(addToDatabase(tournament.id, "", "", uploadFile, metadata))
    } yield Successful).run
  }

}
