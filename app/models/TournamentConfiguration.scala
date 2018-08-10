package models

import play.api.libs.json._

case class TournamentConfiguration(shouldScoreMatch: Boolean, allowTies: Option[Boolean], pointsToWin: Option[Int]) {
  private def valid: Boolean = {
    if (!shouldScoreMatch) {
      true
    } else {
      allowTies.isDefined && pointsToWin.isDefined
    }
  }

  require(valid)
}
object TournamentConfiguration {
  private val writes: Writes[TournamentConfiguration] = (tc: TournamentConfiguration) => {
    JsObject(
      Seq(
        Some("shouldScoreMatch" -> JsBoolean(tc.shouldScoreMatch)),
        tc.allowTies.map("allowTies" -> JsBoolean(_)),
        tc.pointsToWin.map("pointsToWin" -> JsNumber(_))
      ).flatten
    )
  }

  private val reads: Reads[TournamentConfiguration] = (json: JsValue) => {
    val shouldScoreMatch = (json \ "shouldScoreMatch").as[Boolean]
    val allowTies = (json \ "allowTies").asOpt[Boolean]
    val pointsToWin = (json \ "pointsToWin").asOpt[Int]

    if (shouldScoreMatch && (allowTies.isEmpty || pointsToWin.isEmpty)) {
      JsError("If `shouldScoreMatch` is true, we must have both `allowTies` and `pointsToWin`")
    } else {
      JsSuccess(TournamentConfiguration(shouldScoreMatch, allowTies, pointsToWin))
    }
  }

  implicit val format = Format(reads, writes)
}
