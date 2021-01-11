package game.actors

import akka.actor.typed.{ActorRef, Behavior, Scheduler}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import game.actors.FieldActor.FieldUpdate
import game.actors.GameActor.GameAction
import game.model.Card.Power
import game.model.{Column, Field, PlayerState}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object PlayerActor {
  sealed trait PlayerMessage

  sealed trait PlayerAction extends PlayerMessage
  final case class StandPhaseAction(replyTo : ActorRef[GameAction]) extends PlayerAction
  final case class DeclareAttackAction(column : Column, replyTo : ActorRef[Either[PlayerError, AttackResponse]]) extends PlayerAction

  sealed trait PlayerResponse extends PlayerMessage
  final case class FieldUpdate(field : Field) extends PlayerResponse
  final case class PlayerUpdate(player: PlayerState) extends PlayerResponse
  final case class AttackResponse(playerState: PlayerState, attackPower : Power) extends PlayerResponse


  sealed trait PlayerError extends PlayerMessage
  case object Timeout extends PlayerError
  case object NonExistingAttacker extends PlayerError
  case class Unexpected(exception: Throwable) extends PlayerError

  def apply(_player : PlayerState)(implicit timeout : Timeout) : Behavior[PlayerMessage] =
    Behaviors.setup { context =>
      implicit val ec: ExecutionContextExecutor = context.executionContext
      implicit val scheduler: Scheduler = context.system.scheduler
      var player = _player

      Behaviors.receiveMessage {
        case StandPhaseAction(replyTo) =>
          val fieldActor = context.spawn(FieldActor(player.field), "CurrentPlayerField")
          context.ask(fieldActor, FieldActor.StandAllAction) {
            case Failure(exception) =>
              println(s"error : ${exception.getMessage}")
              Unexpected(exception)
            case Success(FieldUpdate(field)) =>
              PlayerUpdate(player.copy(field = field))
          }
          Behaviors.same
        case PlayerUpdate(p) =>
          player = p
          println(player)
          Behaviors.same
      }

//        case DeclareAttackAction(column, replyTo) =>
//          val fieldActor = context.spawn(FieldActor(player.field), "CurrentPlayerField")
//          fieldActor.ask(DeclaresAttackAction(column, _)).map {
//            case Right(FieldActor.AttackResponse(field, attackPower)) =>
//              Right(AttackResponse(player.copy(field = field), attackPower))
//            case Left(FieldActor.NonExistingAttacker) =>
//              Left(NonExistingAttacker)
//            case _ =>
//              Left(Timeout)
//          }
//            .onComplete {
//              case Success(value) =>
//                replyTo ! value
//              case Failure(ex) =>
//                replyTo ! Left(Unexpected(ex))
//            }
//          Behaviors.stopped
    }
}
