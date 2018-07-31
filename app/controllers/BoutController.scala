package controllers

import database.BoutDao
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import scalaz.{-\/, \/-}
import services._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BoutController @Inject() (boutPersister: BoutPersister, boutDao: BoutDao, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

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

  def upload = Action.async(parse.multipartFormData) { implicit request =>
    request.body.file("file") match {
      case None => Future.successful(BadRequest("No file specified"))
      case Some(realFile) =>
        val data = request.body.asFormUrlEncoded
        boutPersister.persist(data, realFile.ref.path).map {
          case -\/(error)   => InternalServerError(error.toString)
          case \/-(success) => Ok("Persisted")
        }
    }

//    request.body.file("file") match {
//      case None => Future.successful(BadRequest("No file specified"))
//      case Some(realFile) =>
//        val data = request.body.asFormUrlEncoded
//
//        boutPersister.persist(data, realFile.ref.path).map {
//          case Successful => Ok("persisted")
//          case InvalidMetadata => BadRequest("invalid tournament metdata")
//          case InvalidZipFile => BadRequest("invalid zip file uploaded")
//          case BadTournamentId => BadRequest("Unknown tournament Id")
//        }
//    }

  }
}
