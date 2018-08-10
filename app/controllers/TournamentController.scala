package controllers

import com.typesafe.config.Config
import config.SpyPartyReplayUploaderConfig
import database.{BoutDao, TournamentDao}
import javax.inject.{Inject, Singleton}
import models.{TournamentBoutInfo, TournamentInput}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TournamentController @Inject()(config: SpyPartyReplayUploaderConfig, boutDao: BoutDao, tournamentDao: TournamentDao, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAll = Action.async { implicit request =>
    tournamentDao.getAll.map { res =>
      Ok(Json.toJson(res))
    }
  }

  def getById(x: Int) = Action.async { implicit request =>
    tournamentDao.getById(x).map {
      case Some(tournament) => Ok(Json.toJson(tournament))
      case None             => NotFound(s"Tournament with id $x not found")
    }
  }

  def getBoutsForTournament(x: Int) = Action.async { implicit request =>
    val start = request.getQueryString("start").map(_.toInt).getOrElse(config.DefaultStart)
    val count = Math.min(request.getQueryString("count").map(_.toInt).getOrElse(config.DefaultCount), config.MaxCount)

    for {
      bouts     <- boutDao.getByTournamentId(x, start, count)
      boutCount <- boutDao.getCountForTournament(x)
    } yield {
      Ok(Json.toJson(TournamentBoutInfo(x, boutCount, bouts)))
    }
  }

  def insertNew = Action.async(parse.json) { implicit request =>
    request.headers.get("X-Authorization") match {
      case Some(x) if x == config.TournamentCreationPassword =>
        request.body.validate[TournamentInput] match {
          case JsError(error) => Future.successful(BadRequest(s"Invalid JSON: $error"))
          case JsSuccess(tournament, _) => tournamentDao.insert(tournament)
            .map(_ => Ok(s"New tournament creation successful"))
        }

      case _ => Future.successful(Unauthorized("invalid password"))
    }
  }
}
