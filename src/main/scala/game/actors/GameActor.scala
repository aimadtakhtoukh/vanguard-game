package game.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{Behavior, Scheduler}
import akka.util.Timeout
import game.actors.PlayerActor.{AttackResponse, DeclareAttackAction}
import game.model.{Column, GameState, Position}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

object GameActor {
  sealed trait GameMessage
  sealed trait GameAction extends GameMessage
  case object TurnStartAction extends GameAction
  case object StandPhaseStartAction extends GameAction
  case class AttackAction(source : Column, target : Position) extends GameAction

  sealed trait GameResponse extends GameMessage
  case class Message(message : String) extends GameResponse

  def apply(_game : GameState) : Behavior[GameMessage] =
    Behaviors.setup { context =>
      implicit val timeout: Timeout = 3 seconds
      implicit val ec: ExecutionContextExecutor = context.executionContext
      implicit val scheduler: Scheduler = context.system.scheduler

      var game = _game

      Behaviors.receiveMessage {
        case TurnStartAction =>
          game = game.copy(current = game.opponent, opponent = game.current)
          Behaviors.same

        case StandPhaseStartAction =>
          val currentPlayerActor = context.spawn(PlayerActor(game.current), "CurrentPlayer")
//          context.ask(currentPlayerActor, PlayerActor.StandPhaseAction) {
//
//          }
          Behaviors.same

        case AttackAction(source, target) =>
          val currentPlayerActor = context.spawn(PlayerActor(game.current), "CurrentPlayer")
          context.ask(currentPlayerActor, DeclareAttackAction(source, _)) {
            case Failure(_) => Message("fail")
            case Success(Left(playerError)) => Message(playerError.toString)
            case Success(Right(AttackResponse(playerState, attackPower))) =>
              Message(s"$playerState, $attackPower")
          }
          Behaviors.same
        case Message(message) =>
          println(message)
          Behaviors.same
      }
    }
}

