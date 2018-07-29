package database

import javax.inject.{Inject, Singleton}
import models.{Tournament, TournamentSubmissionForm}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TournamentDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private def convertFromDatabase(dbRow: Tables.TournamentsRow): Tournament = {
    Tournament(dbRow.id, dbRow.name, dbRow.active, Json.parse(dbRow.formItems).as[TournamentSubmissionForm])
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
}
