package game.model.start

import game.model.Card
import game.model.PlayerState.Deck

case class PlayerStartingInfo(
  firstVanguard : Card,
  deck : Deck
)
