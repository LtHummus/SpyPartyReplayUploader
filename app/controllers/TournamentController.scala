package controllers

import com.typesafe.config.Config
import database.TournamentDao
import javax.inject.{Inject, Singleton}
import models.TournamentInput
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TournamentController @Inject() (config: Config, tournamentDao: TournamentDao, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

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

  def insertNew = Action.async(parse.json) { implicit request =>
    request.headers.get("X-Authorization") match {
      case Some(x) if x == config.getString("") =>
        request.body.validate[TournamentInput] match {
          case JsError(error) => Future.successful(BadRequest(s"Invalid JSON: $error"))
          case JsSuccess(tournament, _) => tournamentDao.insert(tournament)
            .map(_ => Ok(s"New tournament creation successful"))
        }

      case _ => Future.successful(Unauthorized("invalid password"))
    }
  }
}
