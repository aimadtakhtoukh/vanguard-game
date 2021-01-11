package game.model

import PlayerState.{Deck, Hand}

case class PlayerState(
  field : Field,
//  hand : Hand,
//  deck : Deck
)

object PlayerState {
  type Deck = List[Card]
  type Hand = List[Card]
}
