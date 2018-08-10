package database

import database.Tables.{BoutsRow, GamesRow}
import javax.inject.{Inject, Singleton}
import models.Bout
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import replays.{Mission, Replay}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class BulkPersistUtil @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.profile.api._


  def persistAllBoutData(bout: Bout, games: Seq[Replay]) = {
    val newBout = BoutsRow(0, bout.tournament, bout.player1, bout.player2, bout.url, Json.toJson(bout.metadata).toString())

    val insertion = (for {
      createdBout <- Tables.Bouts.returning(Tables.Bouts.map(_.id)).into((bout, id) => bout.copy(id = id)) += newBout
      newGames = games.map(curr =>
        GamesRow(0,
          createdBout.id,
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
      _ <- Tables.Games ++= newGames
    } yield ()).transactionally

    dbConfig.db.run(insertion)
  }
}
