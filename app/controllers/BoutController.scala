package controllers

import database.BoutDao
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class BoutController @Inject() (boutDao: BoutDao, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getById(x: Int) = Action.async {
    boutDao.getById(x).map {
      case Some(bout) => Ok(Json.toJson(bout))
      case None       => NotFound(s"Bout with id $x not found")
    }
  }

  def getByTournamentId(x: Int) = Action.async {
    boutDao.getByTournamentId(x).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
