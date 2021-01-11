package game.model

import Card.{Clan, Name, Power}

case class Card(
  name : Name,
  clan : Clan,
  power : Power
)

object Card {
  type Name = String
  type Clan = String
  type Power = Int
}