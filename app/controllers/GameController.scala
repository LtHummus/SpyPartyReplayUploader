package controllers

import database.GameDao
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

@Singleton
class GameController @Inject() (cc: ControllerComponents, gameDao: GameDao)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getByBoutId(x: Int) = Action.async {
    gameDao.getByBout(x).map { res =>
      Ok(Json.toJson(res))
    }
  }
}
