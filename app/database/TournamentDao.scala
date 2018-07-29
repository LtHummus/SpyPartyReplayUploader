package database

import database.Tables.TournamentsRow
import javax.inject.{Inject, Singleton}
import models.{Tournament, TournamentInput, TournamentSubmissionForm}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import scalacache.Cache
import scalacache.caffeine.CaffeineCache
import scalacache.memoization._
import scalacache.modes.scalaFuture._
import scala.concurrent.duration._
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

  //XXX: this can be dangerous because the caches may get out of sync...for example, a tournament might appear in the
  //     individual cache, but not be present in the full tournament cache. The proper way to do this, would be to cache
  //     with a map of all tournaments that the all() pulls from...but how to refresh? This might need a more custom
  //     cache solution. We're not caching indivual tournaments for now, since we need to dig in to scalacache a bit more
  //     because we don't want to cache the non-existence of a tournament...perhaps using caffeine directly is better?
  implicit val fullTournamentCache: Cache[Seq[Tournament]] = CaffeineCache[Seq[Tournament]]

  import dbConfig.profile.api._

  def getAll: Future[Seq[Tournament]] = memoizeF[Future, Seq[Tournament]](Some(10.minutes)) {
    dbConfig.db.run {
      Tables.Tournaments.result.map { res =>
        res.map(convertFromDatabase)
      }
    }
  }

  //TODO: cache
  def getById(id: Int): Future[Option[Tournament]] = {
    dbConfig.db.run {
      Tables.Tournaments.filter(_.id === id).result.headOption.map { it =>
        it.map(convertFromDatabase)
      }
    }
  }

  def insert(newTournament: TournamentInput): Future[Int] = {
    //XXX: right now, we just purge the full tournament cache to keep things from getting too out of sync, see large
    //     note above
    fullTournamentCache.removeAll()
    val newRow = convertFromInput(newTournament)
    dbConfig.db.run {
      Tables.Tournaments += newRow
    }
  }
}
