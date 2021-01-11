package game

import akka.actor.typed.ActorSystem
import game.actors.GameActor
import game.actors.GameActor.{AttackAction, GameAction}
import game.model._

import scala.concurrent.{ExecutionContextExecutor, Future}

object Application extends App {

  val royalCard = Card(name = "Stardust Trumpeter", clan = "Royal Paladin", power = 8000)
  val firstPlayerState = PlayerState(
    field = Field()
      .update(FilledCircle(position = Vanguard, card = royalCard, posture = Rest))
      .update(FilledCircle(position = LowerMiddle, card = royalCard, posture = Rest))
  )
  val kageroCard = Card(name = "Lizard Runner, Undeux", clan = "Kagero", power = 8000)
  val secondPlayerState = PlayerState(
    field = Field().update(FilledCircle(position = Vanguard, card = kageroCard, posture = Stand))
  )

  val gameState = GameState(firstPlayerState, secondPlayerState)

  implicit val actorSystem : ActorSystem[GameAction] = ActorSystem(GameActor(gameState), "VanguardGame")
  implicit val ec: ExecutionContextExecutor = actorSystem.executionContext

//  Future {
//    actorSystem ! AttackAction(VanguardColumn, Vanguard)
//    actorSystem ! GameActor.TurnStartAction
    actorSystem ! GameActor.StandPhaseStartAction
//  }.andThen {
//    _ =>
//      actorSystem.terminate()
//  }


}
