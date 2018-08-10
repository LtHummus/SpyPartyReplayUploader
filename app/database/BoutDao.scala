package database

import database.Tables.BoutsRow
import javax.inject.{Inject, Singleton}
import models.{Bout, BoutMetadata}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BoutDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private[database] def convertToBout(x: BoutsRow): Bout = {
    Bout(x.id, x.tournament, x.player1, x.player2, x.url, Json.parse(x.metadata).as[BoutMetadata])
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

  def getByTournamentId(x: Int, start: Int = 0, count: Int = 20): Future[Seq[Bout]] = {
    dbConfig.db.run {
      Tables.Bouts.filter(_.tournament === x).drop(start).take(count).result.map { res =>
        res.map(convertToBout)
      }
    }
  }

  def getCountForTournament(x: Int): Future[Int] = {
    dbConfig.db.run {
      Tables.Bouts.filter(_.tournament === x).size.result
    }
  }

  def insert(bout: Bout): Future[Int] = {
    val newBout = BoutsRow(0, bout.tournament, bout.player1, bout.player2, bout.url, Json.toJson(bout.metadata).toString())
    dbConfig.db.run {
      Tables.Bouts.returning(Tables.Bouts.map(_.id)).into((bout, id) => bout.copy(id = id)) += newBout
    }.map(_.id)
  }
}
