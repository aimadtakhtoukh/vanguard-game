package game.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout
import game.actors.CircleActor.CircleState
import game.actors.PlayerActor.PlayerResponse
import game.model.Card.Power
import game.model.{Column, Field, Position, Rest}

import scala.util.{Failure, Success}

object FieldActor {
  sealed trait FieldMessage

  sealed trait FieldAction extends FieldMessage
  final case class StandAllAction(replyTo: ActorRef[PlayerResponse]) extends FieldAction
  final case class DeclaresAttackAction(column: Column, replyTo : ActorRef[Either[FieldError, AttackResponse]]) extends FieldAction

  sealed trait FieldResponse extends FieldMessage
  case class FieldUpdate(field : Field, replyTo: ActorRef[PlayerResponse]) extends FieldResponse
  case object Noop extends FieldResponse
  case class AttackResponse(field : Field, attackPower : Power) extends FieldResponse

  sealed trait FieldError extends FieldMessage
  case object NonExistingAttacker extends FieldError
  case object Unexpected extends FieldError

  def apply(_field : Field)(implicit timeout : Timeout) : Behavior[FieldMessage] =
    Behaviors.setup { context =>
      var field = _field

      Behaviors.receiveMessage {
        case StandAllAction(replyTo) =>
          Position.values
            .map(field.circle)
            .map(CircleActor(_))
            .zipWithIndex
            .map { case (behavior, index) => context.spawn(behavior, s"CircleActor$index") }
            .foreach(actor => context.ask(actor, CircleActor.StandAction) {
              case Failure(exception) =>
                println(s"error : ${exception.getMessage}")
                Unexpected
              case Success(CircleState(circle)) =>
                FieldUpdate(field.update(circle), replyTo)
              case Success(CircleActor.Noop) =>
                Noop
            })
          Behaviors.same
        case Unexpected =>
          Behaviors.same
        case FieldUpdate(f, replyTo) =>
          field = f
          replyTo ! PlayerActor.FieldUpdate(field)
          Behaviors.same
        case Noop =>
          Behaviors.same
        case DeclaresAttackAction(column, replyTo) =>
          val attackerCircle = field.filledCircle(column.forwardPosition).map(_.copy(posture = Rest))
          val boosterCircle = field.filledCircle(column.backPosition).map(_.copy(posture = Rest))

          val updatedField = field.update(attackerCircle).update(boosterCircle)

          val attackerPower: Either[FieldError, Power] = attackerCircle.map(_.card).map(_.power).toRight[FieldError](NonExistingAttacker)
          val boosterPower : Power = boosterCircle.map(_.card).map(_.power).getOrElse(0)
          val attackPower = attackerPower.map(_ + boosterPower)

          val message = attackPower.map(AttackResponse(updatedField, _))
          replyTo ! message
          Behaviors.stopped
      }
    }

}
