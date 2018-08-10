package database

import database.Tables.GamesRow
import javax.inject.{Inject, Singleton}
import models.Game
import play.api.db.slick.DatabaseConfigProvider
import replays.{Level, Mission, Replay}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GameDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private[database] def convertToGame(x: GamesRow): Game = {
    Game(
      x.id,
      x.bout,
      x.spy,
      x.sniper,
      x.result,
      Level.getLevelByChecksum(x.level).map(_.name).getOrElse("Unknown"),
      x.loadout,
      x.uuid,
      x.version,
      Mission.listFromInt(x.selectedMissions).map(_.toString),
      Mission.listFromInt(x.pickedMissions).map(_.toString),
      Mission.listFromInt(x.accomplishedMissions).map(_.toString),
      x.startDurationSeconds,
      x.numGuests
    )
  }

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.profile.api._

  def getById(x: Int): Future[Option[Game]] = {
    dbConfig.db.run {
      Tables.Games.filter(_.id === x).result.headOption.map { res =>
        res.map(convertToGame)
      }
    }
  }

  def getByBout(x: Int): Future[Seq[Game]] = {
    dbConfig.db.run {
      Tables.Games.filter(_.bout === x).result.map { res =>
        res.map(convertToGame)
      }
    }
  }

  def bulkInsertFromReplay(bout: Int, games: List[Replay]) = {

    val newGames = games.map(curr =>
      GamesRow(0,
        bout,
        curr.spy,
        curr.sniper,
        curr.result.internalId.toByte,
        curr.level.checksum,
        curr.loadoutType.toString,
        curr.uuid,
        curr.version.toByte,
        Mission.toInt(curr.selectedMissions),
        Mission.toInt(curr.pickedMissions),
        Mission.toInt(curr.completedMissions),
        curr.startDuration,
        curr.numGuests)
    )

    dbConfig.db.run {
      Tables.Games ++= newGames
    }
  }

}
