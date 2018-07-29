package services

import java.nio.file.Path

import cats.implicits._
import cats.data.OptionT
import database.{BoutDao, TournamentDao}
import javax.inject.{Inject, Singleton}
import models.{BoutMetadata, Tournament}

import scala.concurrent.{ExecutionContext, Future}

sealed trait PersistResult
case object BadTournamentId extends PersistResult
case object InvalidMetadata extends PersistResult
case object InvalidZipFile extends PersistResult
case object Successful extends PersistResult


@Singleton
class BoutPersister @Inject() (boutDao: BoutDao, tournamentDao: TournamentDao) {

  private def getTournament(id: Int): Future[Option[Tournament]] = tournamentDao.getById(id)
  private def validateData(tournament: Tournament, data: Map[String, Seq[String]]): Option[BoutMetadata] = {
    val requiredFieldNames = tournament.formItems.items.map(_.internalName).toSet
    val fieldsWeHave = data.keys.toSet

    val missingFields = requiredFieldNames -- fieldsWeHave

    //TODO: validate selection choices here to make sure they match the definition

    if (missingFields.isEmpty) {
      None //TODO: make this a disjunction with a "missing fields" error
    } else {
      //TODO: turn all fields in to Items in the model
    }

    ???


  }

  def persist(data: Map[String, Seq[String]], file: Path)(implicit ec: ExecutionContext): Future[PersistResult] = {

    val res = for {
      x <- OptionT(getTournament(4))
    } yield x

    res.value.map { res =>
      println(res)
      Successful
    }


  }

}
