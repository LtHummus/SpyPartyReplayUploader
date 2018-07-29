package database

import database.Tables.TournamentsRow
import javax.inject.{Inject, Singleton}
import models.{Tournament, TournamentInput, TournamentSubmissionForm}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TournamentDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private def convertFromDatabase(dbRow: Tables.TournamentsRow): Tournament = {
    Tournament(dbRow.id, dbRow.name, dbRow.active, Json.parse(dbRow.formItems).as[TournamentSubmissionForm])
  }

  private def convertFromInput(input: TournamentInput) = {
    TournamentsRow(0, input.name, input.active, Json.toJson(input.formItems).toString())
  }

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.profile.api._

  def getAll: Future[Seq[Tournament]] = {
    dbConfig.db.run {
      Tables.Tournaments.result.map { res =>
        res.map(convertFromDatabase)
      }
    }
  }

  def getById(id: Int): Future[Option[Tournament]] = {
    dbConfig.db.run {
      Tables.Tournaments.filter(_.id === id).result.headOption.map { it =>
        it.map(convertFromDatabase)
      }
    }
  }

  def insert(newTournament: TournamentInput): Future[Int] = {
    val newRow = convertFromInput(newTournament)
    dbConfig.db.run {
      Tables.Tournaments += newRow
    }
  }
}
