package database

import database.Tables.BoutsRow
import javax.inject.{Inject, Singleton}
import models.Bout
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BoutDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private def convertToBout(x: BoutsRow): Bout = {
    Bout(x.id, x.tournament, x.player1, x.player2, x.url, Json.parse(x.metadata))
  }

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.profile.api._

  def getById(x: Int): Future[Option[Bout]] = {
    dbConfig.db.run {
      Tables.Bouts.filter(_.id === x).result.headOption.map { res =>
        res.map(convertToBout)
      }
    }
  }

  def getByTournamentId(x: Int): Future[Seq[Bout]] = {
    dbConfig.db.run {
      Tables.Bouts.filter(_.tournament === x).result.map { res =>
        res.map(convertToBout)
      }
    }
  }
}
