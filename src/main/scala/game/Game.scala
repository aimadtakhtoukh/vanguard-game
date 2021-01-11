package game

import game.model.start.PlayerStartingInfo
import game.model._

class Game {

  def initGame(first : PlayerStartingInfo, second : PlayerStartingInfo): GameState =
    GameState(
      toPlayerState(first),
      toPlayerState(second)
    )

  private def toPlayerState(startingInfo: PlayerStartingInfo) = {
    model.PlayerState(
      Field().update(FilledCircle(position = Vanguard, posture = Stand, card = startingInfo.firstVanguard))
    )
  }
}
