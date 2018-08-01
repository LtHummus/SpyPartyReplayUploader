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
      //note: this a little "unpure" because the PersistResult object really belongs to the parser, but we're
      //      using it here
      case None => Future.successful(BadRequest(Json.toJson(PersistResult(InvalidZipFile, "Zip file is missing"))))
      case Some(realFile) =>
        val data = request.body.asFormUrlEncoded
        boutPersister.persist(data, realFile.ref.path).map { res =>
          val serializedResult = Json.toJson(res)
          res.kind match {
            case BadTournamentId => BadRequest(serializedResult)
            case BadMetadata => PreconditionFailed(serializedResult)
            case InvalidZipFile => BadRequest(serializedResult)
            case FailedToAddToDatabase => InternalServerError(serializedResult)
            case UploadFailure => InternalServerError(serializedResult)
            case Successful => Ok(serializedResult)
          }
        }
    }

  }
}
