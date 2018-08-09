package replays

sealed trait Mission
case object BugAmbassador extends Mission
case object ContactDoubleAgent extends Mission
case object TransferMicrofilm extends Mission
case object SwapStatue extends Mission
case object InspectStatues extends Mission
case object SeduceTarget extends Mission
case object PurloinGuestList extends Mission
case object FingerprintAmbassador extends Mission

object Mission {

  val AllMissions: List[Mission] = List(BugAmbassador,
    ContactDoubleAgent,
    TransferMicrofilm,
    SwapStatue,
    InspectStatues,
    SeduceTarget,
    PurloinGuestList,
    FingerprintAmbassador)

  def listFromInt(data: Int): List[Mission] = {
    AllMissions.zipWithIndex.filterNot{ case (_, x) => (data & (1 << x)) == 0 }.map(_._1)
  }
}