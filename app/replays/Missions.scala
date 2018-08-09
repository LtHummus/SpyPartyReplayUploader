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

  private val MissionsWithIndex: List[(Mission, Int)] = AllMissions.zipWithIndex

  def listFromInt(data: Int): List[Mission] = {
    MissionsWithIndex.filterNot{ case (_, x) => (data & (1 << x)) == 0 }.map(_._1)
  }

  def toInt(missions: List[Mission]): Int = {
    val missionSet = missions.toSet
    MissionsWithIndex.filter{ case (m, _) => missionSet.contains(m) }.map{ case (_, x) => 1 << x }.fold(0)((state, curr) => state | curr)
  }
}