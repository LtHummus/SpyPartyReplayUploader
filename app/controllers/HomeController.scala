package controllers

import javax.inject._
import models._
import play.api._
import play.api.libs.json.Json
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def sampleForm() = Action { implicit request =>
    val f = TournamentSubmissionForm(Seq(
      TournamentSubmissionFormItem("week", "Week", NumberMetadata(0, 10)),
      TournamentSubmissionFormItem("day", "day", NumberMetadata(1, 7)),
      TournamentSubmissionFormItem("start", "Starter", StringMetadata(400)),
      TournamentSubmissionFormItem("division", "Division", SelectionMetadata(
        Seq(
          SelectionEntry("challenger", "Challenger"),
          SelectionEntry("diamond", "Diamond")
        ))
      )
    ))
    Ok(Json.toJson(f))

  }

  def parseForm() = Action(parse.json) { request =>
    val potentialForm = request.body.validate[TournamentSubmissionForm]
    potentialForm.fold(
      errors => ???,
      form => {
        println(form)
        Ok("ok")
      }
    )
  }
}
