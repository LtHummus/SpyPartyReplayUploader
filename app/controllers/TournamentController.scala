package controllers

import database.TournamentDao
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class TournamentController @Inject() (tournamentDao: TournamentDao, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

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
}
