import scala.annotation.tailrec

package object replays {

  //THIS IS REALLY BAD AND PROBABLY HAS TONS OF CODE SMELL, BUT HELL, WE ARE DOING IT
  //UNTIL SANER HEADS PREVAIL, WHICH IS NOT RIGHT NOW
  implicit class RichReplayList(replays: List[Replay]) {

    val orderedReplays: List[Replay] = replays.filter(_.isCompleted).sorted

    val player1: String = orderedReplays.head.spy
    val player2: String = orderedReplays.head.sniper

    val player1Score: Int = orderedReplays.count(_.winnerName == player1)
    val player2Score: Int = orderedReplays.count(_.winnerName == player2)

    val isTie: Boolean = player1Score == player2Score


    private def getSummaryForGroup(games: List[Replay]) = {
      if (games.size == 1) {
        val onlyGame = games.head
        s"${onlyGame.winnerName} wins as ${onlyGame.winnerRole} on ${onlyGame.fullLevelName}"
      } else {
        val game1winner = games.head.winnerName
        val game1winnerRole = games.head.winnerRole
        val game2winnerRole = games(1).winnerRole

        (game1winnerRole, game2winnerRole) match {
          case ("spy", "spy")       => s"Spies win ${games.head.fullLevelName}"
          case ("sniper", "sniper") => s"Snipers win ${games.head.fullLevelName}"
          case _                    => s"$game1winner sweeps ${games.head.fullLevelName}"
        }
      }
    }

    private def getGameSummaryForManyGames(games: List[Replay]): String = {
      if (games.size == 1) {
        val onlyGame = games.head
        s"${onlyGame.winnerName} wins as ${onlyGame.winnerRole}"
      } else if (games.forall(_.winnerRole == "spy")) {
        s"Spy wins ${games.length} games"
      } else if (games.forall(_.winnerRole == "sniper")) {
        s"Sniper wins ${games.length} games"
      } else if (games.forall(_.winnerName == player1)) {
        s"$player1 wins ${games.length} games"
      } else if (games.forall(_.winnerName == player2)) {
        s"$player2 wins ${games.length} games"
      } else {
        games.map(x => s"${x.smallDescription}").mkString("<br />")
      }

    }

    private def getGamesForLayout(haystack: List[Replay], layout: String): List[Replay] = {
      @tailrec def internal(games: List[Replay], buffer: List[Replay]): List[Replay] = {
        if (games.isEmpty) {
          buffer
        } else if (games.head.fullLevelName == layout) {
          internal(games.tail, buffer :+ games.head)
        } else {
          buffer
        }
      }

      internal(haystack, List())
    }

    def getForumGameSummary: String = {
      @tailrec def internal(games: List[Replay], buffer: String): String = {
        if (games.isEmpty) {
          buffer
        } else {
          val currentMapConfiguration = games.head.fullLevelName

          //find all games that were played on this
          val ourGames = getGamesForLayout(games, currentMapConfiguration)
          val newBuffer = buffer + "[b]" +  currentMapConfiguration + "[/b]<br />" + getGameSummaryForManyGames(ourGames) + "<br /><br />"

          internal(games.drop(ourGames.length), newBuffer)
        }
      }

      internal(orderedReplays, "")
    }

    def getGameSummary: List[String] = {
      orderedReplays.map(_.description)
    }

    def getScoreLine: String = {
      if (player1Score > player2Score) {
        s"$player1 wins $player1Score-$player2Score"
      } else if (player2Score > player1Score) {
        s"$player2 wins $player2Score-$player1Score"
      } else {
        s"TieParty $player1Score-$player2Score"
      }
    }
  }

}
