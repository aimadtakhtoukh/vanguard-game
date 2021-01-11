package game.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import game.model.{Circle, EmptyCircle, FilledCircle, Stand}

object CircleActor {
  sealed trait CircleMessage

  sealed trait CircleAction extends CircleMessage
  case class StandAction(ref : ActorRef[CircleMessage]) extends CircleAction

  sealed trait CircleResponse extends CircleMessage
  case class CircleState(circle : Circle) extends CircleResponse
  case object Noop extends CircleResponse

  def apply(circle : Circle): Behavior[CircleMessage] =
    Behaviors.setup { context =>
      circle match {
        case c: FilledCircle => filledCircleBehavior(c)
        case EmptyCircle(_) => emptyCircleBehavior
      }
    }

  def filledCircleBehavior(c : FilledCircle) : Behavior[CircleMessage] =
    Behaviors.receiveMessage {
      case StandAction(actorRef: ActorRef[CircleMessage]) =>
        actorRef ! CircleState(c.copy(posture = Stand))
        Behaviors.stopped
    }

  def emptyCircleBehavior : Behavior[CircleMessage] =
    Behaviors.receiveMessage {
      case StandAction(actorRef: ActorRef[CircleMessage]) =>
        actorRef ! Noop
        Behaviors.stopped
    }

}
